package ml.rektsky.spookysky.processor

import org.objectweb.asm.tree.ClassNode

abstract class Processor {

    abstract fun process(node: ClassNode): Boolean

}