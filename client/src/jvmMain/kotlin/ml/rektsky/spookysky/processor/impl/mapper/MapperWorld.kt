package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.mapping.mappings.world.MapChunk
import ml.rektsky.spookysky.mapping.mappings.world.block.MapBlockPos
import ml.rektsky.spookysky.mapping.mappings.world.MapWorld
import ml.rektsky.spookysky.mapping.mappings.world.block.MapIBlockState
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.impl.templates.MapperStringSearcher
import ml.rektsky.spookysky.utils.ChatColor
import ml.rektsky.spookysky.utils.DescriptorUtil
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.TypeInsnNode
import java.lang.reflect.Modifier

class MapperWorld: MapperStringSearcher("checkLight", MapWorld) {

    override fun process0(loadedClass: LoadedClass) {
        super.process0(loadedClass)
        if (MapWorld.isMapped()) {
            Client.debug("Hello: ${MapWorld.mapped!!.classNode.name}", ChatColor.GREEN)
            for (methodNode in loadedClass.classNode.methods.filter {
                it.instructions.any { it is LdcInsnNode && it.cst == "checkLight" } &&
                it.instructions.none { it is TypeInsnNode && it.desc.contains("CallbackInfoReturnable") }
            }) {
                Client.debug("Found ${methodNode.name}")
                if (!methodNode.desc.startsWith("(II")) {
                    MapWorld.mapSetBlockState.mapped = methodNode
                    Client.debug("BlockPos: ${DescriptorUtil.getParameterTypeNames(methodNode.desc)[0]}")
                    Client.debug("IBlockState: ${DescriptorUtil.getParameterTypeNames(methodNode.desc)[1]}")
                    scheduleClassLoadAction(DescriptorUtil.getParameterTypeNames(methodNode.desc)[0].let { it.substring(1, it.length - 1) }) {
                        MapBlockPos.mapped = it
                    }
                    scheduleClassLoadAction(DescriptorUtil.getParameterTypeNames(methodNode.desc)[1].let { it.substring(1, it.length - 1) }) {
                        MapIBlockState.mapped = it
                    }
                } else {
                    Client.debug("Chunk: ${DescriptorUtil.getParameterTypeNames(methodNode.desc)[2]}")
                    MapWorld.mapPlayMoodSoundAndCheckLight.mapped = methodNode
                    scheduleClassLoadAction(DescriptorUtil.getParameterTypeNames(methodNode.desc)[2].let { it.substring(1, it.length - 1) }) {
                        MapChunk.mapped = it
                    }
                }
            }
        }
    }
}