package ml.rektsky.spookysky.shellcode

import org.objectweb.asm.tree.InsnList

abstract class ShellCode {

    abstract fun generate(): InsnList

}