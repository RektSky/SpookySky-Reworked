package ml.rektsky.spookysky.utils

operator fun String.times(number: Int): String {
    var buffer = ""
    for (i in 0 until number) {
        buffer += this
    }
    return buffer
}