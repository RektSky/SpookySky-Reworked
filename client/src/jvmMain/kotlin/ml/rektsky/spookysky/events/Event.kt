package ml.rektsky.spookysky.events

import ml.rektsky.spookysky.shellcode.impl.ShellCodeInvokeMethod
import ml.rektsky.spookysky.utils.DescriptorUtil
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode

abstract class Event {

    fun callEvent() {
        EventsManager.callEvent(this)
    }

    companion object {
        fun generateEventCallingInstructions(eventType: Class<out Event>, parameterGetter: (out: InsnList) -> Unit = {}): InsnList {
            var out = InsnList()
            out.add(TypeInsnNode(Opcodes.NEW, eventType.name.replace(".", "/")))
            out.add(InsnNode(Opcodes.DUP))
            parameterGetter(out)
            out.add(MethodInsnNode(Opcodes.INVOKESPECIAL,
                eventType.name.replace(".", "/"),
                "<init>",
                DescriptorUtil.getDescriptor(Void.TYPE, *eventType.constructors[0].parameterTypes)
            ))
            out.add(ShellCodeInvokeMethod(Event::class.java.getDeclaredMethod("callEvent")).generate())
            return out
        }
    }


}