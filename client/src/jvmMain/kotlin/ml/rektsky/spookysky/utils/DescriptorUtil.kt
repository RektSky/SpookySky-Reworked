package ml.rektsky.spookysky.utils

object DescriptorUtil {

    fun getReturnType(descriptor: String): String {
        val s = descriptor.split("\\)".toRegex()).toTypedArray()[1]
        return s
    }

    fun getParameterTypeNames(descriptor: String): List<String> {
        val out = ArrayList<String>()
        var buffer = ""
        for (c in descriptor) {
            if (c == ')') return out
            if (c == '(') continue
            buffer += c
            val descriptorToClass = descriptorToClassName(buffer)
            if (descriptorToClass != null) {
                out.add(descriptorToClass)
                buffer = ""
            }
        }
        return out
    }

    fun getParameterTypes(descriptor: String): List<Class<*>> {
        val out = ArrayList<Class<*>>()
        var buffer = ""
        for (c in descriptor) {
            if (c == ')') return out
            if (c == '(') continue
            buffer += c
            val descriptorToClass = descriptorToClass(buffer)
            if (descriptorToClass != null) {
                out.add(descriptorToClass)
                buffer = ""
            }
        }
        return out
    }

    fun descriptorToClassName(s: String): String? {
        var type: Class<*>? = null
        when (s) {
            "Z" -> return "Z"
            "C" -> return "C"
            "B" -> return "B"
            "S" -> return "S"
            "I" -> return "I"
            "J" -> return "J"
            "F" -> return "F"
            "D" -> return "D"
            "V" -> return "V"
        }
        if (s.startsWith("L") && s.endsWith(";")) {
            return "L" + s.substring(1, s.length - 1) + ";"
        }
        return null
    }

    fun descriptorToClass(s: String): Class<*>? {
        var type: Class<*>? = null
        when (s) {
            "Z" -> type = Boolean::class.javaPrimitiveType
            "C" -> type = Char::class.javaPrimitiveType
            "B" -> type = Byte::class.javaPrimitiveType
            "S" -> type = Short::class.javaPrimitiveType
            "I" -> type = Int::class.javaPrimitiveType
            "J" -> type = Long::class.javaPrimitiveType
            "F" -> type = Float::class.javaPrimitiveType
            "D" -> type = Double::class.javaPrimitiveType
            "V" -> type = Void.TYPE
        }
        if (s.startsWith("L") && s.endsWith(";")) {
            type = Class.forName(s.substring(1, s.length - 1).replace("/", "."))
        }
        return type
    }

    fun getDescriptor(returnType: Class<*>, vararg arguments: Class<*>): String? {
        val out = StringBuilder("(")
        for (argument in arguments) {
            out.append(toDescriptorTypeName(argument.name))
        }
        out.append(")").append(toDescriptorTypeName(returnType.name))
        return out.toString()
    }

    fun toDescriptorTypeName(type: String): String {
        return when (type) {
            "byte" -> "B"
            "char" -> "C"
            "double" -> "D"
            "float" -> "F"
            "int" -> "I"
            "long" -> "J"
            "short" -> "S"
            "boolean" -> "Z"
            "void" -> "V"
            else -> {
                if (type.endsWith("[]")) {
                    val s = toDescriptorTypeName(type.substring(0, type.length - 2))
                    return "[" + s.substring(0, s.length)
                }
                val clazzType = type.replace('.', '/')
                if (type.startsWith("[") && type.endsWith(";")) {
                    clazzType
                } else "L$clazzType;"
            }
        }
    }

}