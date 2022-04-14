package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.mapping.mappings.ClientBrandRetriever
import ml.rektsky.spookysky.mapping.mappings.Minecraft
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode

class MapperClientBrandRetriever: Processor() {

    init {
        dependsOn(Minecraft)
    }

    override fun process0(loadedClass: LoadedClass) {
        val minecraft = Minecraft.mapped!!.classNode
        if (loadedClass.classNode.name != minecraft.name) return
        for (method in minecraft.methods) {
            for (instruction in method.instructions) {
                if (instruction is LdcInsnNode && instruction.cst == "client_brand") {
                    val next = instruction.next
                    next as MethodInsnNode
                    scheduleClassLoadAction(next.owner) {
                        ClientBrandRetriever.mapped = it
                        for (method in it.classNode.methods.filter { it.name != "<init>" && it.name != "<clinit>" }) {
                            ClientBrandRetriever.mapGetClientModeName.mapped = method;
                        }
                    }
                }
            }
        }
    }

    override fun jobDone(): Boolean {
        return ClientBrandRetriever.isMapped() && ClientBrandRetriever.mapGetClientModeName.isMapped()
    }


}