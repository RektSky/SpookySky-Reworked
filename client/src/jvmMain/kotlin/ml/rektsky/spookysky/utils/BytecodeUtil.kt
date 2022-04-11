package ml.rektsky.spookysky.utils

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets


object BytecodeUtil {

    fun replace(source: ByteArray, replace: ByteArray, target: ByteArray): ByteArray {
        val outputStream = ByteArrayOutputStream(source.size)
        val sourceLength = source.size
        val replaceLength = replace.size
        val targetLength = target.size
        val replaceLengthR1 = replaceLength - 1
        var i = 0
        root@ while (i < sourceLength) {
            if (i + replaceLength <= sourceLength) {
                for (z in 0 until replaceLength) {
                    if (replace[z] != source[i + z]) {
                        outputStream.write(source[i].toInt())
                        i++
                        continue@root
                    }
                }
                outputStream.write(target, 0, targetLength)
                i += replaceLengthR1
            } else {
                outputStream.write(source[i].toInt())
            }
            i++
        }
        return outputStream.toByteArray()
    }

    fun replace(classfile: ByteArray, const1: String, const2: String): ByteArray {
        return replace(classfile, toJvm(const1), toJvm(const2))
    }

    fun toJvm(const0: String): ByteArray {
        val bytes: ByteArray = const0.toByteArray(StandardCharsets.UTF_8)
        val bos = ByteArrayOutputStream(bytes.size + 2)
        try {
            DataOutputStream(bos).writeShort(bytes.size)
        } catch (ioException: IOException) {
            throw AssertionError(ioException)
        }
        bos.write(bytes, 0, bytes.size)
        return bos.toByteArray()
    }

}