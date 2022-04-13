package ml.rektsky.spookysky.processor.impl

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.mapping.mappings.MapClientBrandRetriever
import ml.rektsky.spookysky.mapping.mappings.MapGetClientModName
import ml.rektsky.spookysky.mapping.mappings.MapMinecraft
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import ml.rektsky.spookysky.processor.ProcessorManager
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode

class PClientBrandRetriever: Processor() {

    init {
        dependsOn(MapMinecraft)
    }

    override fun process0(loadedClass: LoadedClass) {
        val minecraft = MapMinecraft.mapped!!.classNode
        if (loadedClass.classNode.name != minecraft.name) return
        for (method in minecraft.methods) {
            for (instruction in method.instructions) {
                if (instruction is LdcInsnNode && instruction.cst == "client_brand") {
                    val next = instruction.next
                    Client.debug("${next}")
                    next as MethodInsnNode
                    scheduleClassLoadAction(next.owner) {
                        MapClientBrandRetriever.mapped = it
                        for (method in it.classNode.methods) {
                            MapGetClientModName.mapped = method;
                        }
                    }
                }
            }
        }
    }

    override fun jobDone(): Boolean {
        return MapClientBrandRetriever.isMapped() && MapGetClientModName.isMapped()
    }


}