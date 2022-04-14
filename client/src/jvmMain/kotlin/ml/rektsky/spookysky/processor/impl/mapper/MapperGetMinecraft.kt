package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.mapping.mappings.Minecraft
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import ml.rektsky.spookysky.utils.ASMUtils
import ml.rektsky.spookysky.utils.DescriptorUtil
import java.lang.reflect.Modifier

class MapperGetMinecraft: Processor() {

    init {
        dependsOn(Minecraft)
    }


    override fun shouldProcess(loadedClass: LoadedClass): Boolean {
        return loadedClass == Minecraft.mapped
    }

    override fun process0(loadedClass: LoadedClass) {
        val methodNode = loadedClass.classNode.methods.firstOrNull {
            Modifier.isStatic(it.access) &&
                    Modifier.isPublic(it.access) &&
                    it.desc == "()L${Minecraft.mapped!!.classNode.name};"
        } ?: return
        Minecraft.mapGetMinecraft.mapped = methodNode
    }

    override fun jobDone(): Boolean {
        return Minecraft.mapGetMinecraft.isMapped()
    }
}