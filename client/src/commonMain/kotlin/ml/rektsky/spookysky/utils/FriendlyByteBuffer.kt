package ml.rektsky.spookysky.utils



class FriendlyByteBuffer(
    private var buffer: ByteArray
) {

    constructor(size: Int) : this(ByteArray(size))

    private var position = 0
    private var top = 0

    fun putString(string: String) {
        putInt(string.length)
        for (c in string) {
            putChar(c)
        }
    }

    fun nextString(): String {
        val length = nextInt()
        var out = ""
        for (i in 1..length) {
            out += nextChar()
        }
        return out
    }

    fun nextChar(): Char {
        return nextInt().toChar()
    }

    fun putChar(value: Char) {
        putInt(value.code)
    }

    fun putDouble(value: Double) {
        putLong(value.toBits())
    }

    fun nextDouble(): Double {
        return Double.fromBits(nextLong())
    }

    fun putFloat(value: Float) {
        var value1 = value.toBits()
        putInt(value1)
    }

    fun nextFloat(): Float {
        var i = nextInt()
        return Float.fromBits(i)
    }

    fun putLong(value: Long) {
        for (i in 0..7) put((value shr (i*8)).toByte())
    }

    fun nextLong(): Long {
        var result = 0L
        result = result or (next().toLong() and 0xff)
        result = result or (next().toLong() and 0xff shl 8)
        result = result or (next().toLong() and 0xff shl 16)
        result = result or (next().toLong() and 0xff shl 24)
        result = result or (next().toLong() and 0xff shl 32)
        result = result or (next().toLong() and 0xff shl 40)
        result = result or (next().toLong() and 0xff shl 48)
        result = result or (next().toLong() and 0xff shl 56)
        return result
    }

    fun putShort(value: Short) {
        for (i in 0..1) put((value.toInt() shr (i*8)).toByte())
    }

    fun nextShort(): Short {
        var result = 0
        result = result or (next().toInt() and 0xff)
        result = result or (next().toInt() and 0xff shl 8)
        return result.toShort()
    }

    fun putInt(value: Int) {
        for (i in 0..3) put((value shr (i*8)).toByte())
    }

    fun nextInt(): Int {
        var result = 0
        result = result or (next().toInt() and 0xff)
        result = result or (next().toInt() and 0xff shl 8)
        result = result or (next().toInt() and 0xff shl 16)
        result = result or (next().toInt() and 0xff shl 24)
        return result
    }

    fun putList(list: List<(buffer: FriendlyByteBuffer) -> Unit>) {
        putInt(list.size)
        for (content in list) {
            val input = FriendlyByteBuffer(ByteArray(81920))
            content(input)
            val out = input.getArray()
            putInt(out.size)
            putAll(*out)
        }
    }

    fun nextList(): List<FriendlyByteBuffer> {
        val size = nextInt()
        val out = ArrayList<FriendlyByteBuffer>()
        for (i in 0 until size) {
            val contentSize = nextInt()
            val buf = FriendlyByteBuffer(contentSize)
            for (ii in 0 until contentSize) {
                buf.putAll(next())
            }
            buf.flip()
            out.add(buf)
        }
        return out
    }

    fun putBoolean(value: Boolean) {
        put(if (value) 1 else 0)
    }

    fun nextBoolean(): Boolean {
        return next() == 1.toByte()
    }

    fun putAll(vararg bytes: Byte) {
        for (byte in bytes) {
            put(byte)
        }
    }

    fun put(byte: Byte) {
        if (top == -1) throw IllegalStateException("Buffer has not been released yet")
        top += 1
        buffer[top - 1] = byte
    }

    fun next(): Byte {
        if (position == -1) throw IllegalStateException("Buffer has not been flipped yet")
        position += 1
        return buffer[position - 1]
    }

    fun flip() {
        top = -1
        position = 0
    }

    fun release() {
        position = -1
        top = 0
        for (i in 1..buffer.size) {
            buffer[i - 1] = 0
        }
    }

    fun getArray(): ByteArray {
        val out = ByteArray(top)
        for (i in 1..top) {
            out[i - 1] = buffer[i - 1]
        }
        return out
    }


}