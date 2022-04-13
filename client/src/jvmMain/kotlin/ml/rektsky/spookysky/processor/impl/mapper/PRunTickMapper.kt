package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.mapping.mappings.MapMinecraft
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import org.objectweb.asm.tree.LdcInsnNode


const val magicText = "Manually triggered debug crash"


class PRunTickMapper: Processor() {

    init {
        dependsOn(MapMinecraft)
    }


    override fun process0(loadedClass: LoadedClass) {
        for (method in loadedClass.classNode.methods) {
            if (method.instructions.any {it is LdcInsnNode && it.cst == magicText}) {
                MapMinecraft.mapRunTick.mapped = method
                return
            }
        }
    }

    override fun shouldProcess(loadedClass: LoadedClass): Boolean {
        return true
    }

    override fun jobDone(): Boolean {
        return MapMinecraft.mapRunTick.isMapped()
    }


}