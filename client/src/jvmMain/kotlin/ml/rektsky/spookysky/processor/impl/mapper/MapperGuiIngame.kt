package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.mapping.mappings.gui.GuiIngame
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import org.objectweb.asm.tree.LdcInsnNode

class MapperGuiIngame: Processor() {
    override fun process0(loadedClass: LoadedClass) {
        val firstOrNull = loadedClass.classNode.methods.firstOrNull {
            it.instructions.any { insn -> insn is LdcInsnNode && insn.cst == "bossHealth" }
        } ?: return
        GuiIngame.mapped = loadedClass
        GuiIngame.mapRenderGameOverlay.mapped = firstOrNull
    }

    override fun jobDone(): Boolean {
        return GuiIngame.isMapped() && GuiIngame.mapRenderGameOverlay.isMapped()
    }
}