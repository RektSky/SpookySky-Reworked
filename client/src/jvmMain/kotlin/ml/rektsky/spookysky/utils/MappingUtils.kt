package ml.rektsky.spookysky.utils

import org.objectweb.asm.tree.*

object MappingUtils {


    fun hasString(text: String, methodNode: MethodNode): Boolean {
        for (i in 0 until methodNode.instructions.size()) {
            val abstractInsnNode = methodNode.instructions[i]
            if (abstractInsnNode is LdcInsnNode) {
                if (abstractInsnNode.cst is String) {
                    if ((abstractInsnNode.cst as String).contains(text!!)) {
                        return true
                    }
                }
            }
            if (abstractInsnNode is InvokeDynamicInsnNode) {
                for (bsmArg in abstractInsnNode.bsmArgs) {
                    if (bsmArg is String) {
                        if (bsmArg.contains(text!!)) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    fun hasString(text: String, fieldNode: FieldNode): Boolean {
        val value: Any? = fieldNode.value
        if (value is String) {
            if (value.contains(text)) return true
        }
        return false
    }


    fun hasString(text: String, classNode: ClassNode): Boolean {
        var contains = false
        for (method in classNode.methods) {
            if (method == null) continue
            contains = contains or hasString(text, method)
        }
        for (field in classNode.fields) {
            if (field == null) continue
            contains = contains or hasString(text, field)
        }
        return contains
    }

    fun containsString(text: String, methodNode: MethodNode): Boolean {
        for (i in 0 until methodNode.instructions.size()) {
            val abstractInsnNode = methodNode.instructions[i]
            if (abstractInsnNode is LdcInsnNode) {
                if (abstractInsnNode.cst is String) {
                    if (abstractInsnNode.cst == text) {
                        return true
                    }
                }
            }
            if (abstractInsnNode is InvokeDynamicInsnNode) {
                for (bsmArg in abstractInsnNode.bsmArgs) {
                    if (bsmArg is String) {
                        if (bsmArg == text) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    fun containsString(text: String, fieldNode: FieldNode): Boolean {
        val value: Any? = fieldNode.value
        if (value is String) {
            if (value == text) return true
        }
        return false
    }


    fun containsString(text: String, classNode: ClassNode): Boolean {
        var contains = false
        for (method in classNode.methods) {
            contains = contains or containsString(text, method)
        }
        for (field in classNode.fields) {
            contains = contains or containsString(text, field)
        }
        return contains
    }

    fun containsDouble(dvalue: Double, methodNode: MethodNode): Boolean {
        for (i in 0 until methodNode.instructions.size()) {
            val abstractInsnNode = methodNode.instructions[i]
            if (abstractInsnNode is LdcInsnNode) {
                if (abstractInsnNode.cst is Double) {
                    if (abstractInsnNode.cst == dvalue) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun containsDouble(dvalue: Double, fieldNode: FieldNode): Boolean {
        val value: Any? = fieldNode.value
        return if (value is Double) {
            value == dvalue
        } else false
    }


    fun containsDouble(dvalue: Double, classNode: ClassNode): Boolean {
        var contains = false
        for (method in classNode.methods) {
            contains = contains or containsDouble(dvalue, method)
        }
        for (field in classNode.fields) {
            contains = contains or containsDouble(dvalue, field)
        }
        return contains
    }


}