package ml.rektsky.spookysky.utils

class Timer {

    var resetTime = System.currentTimeMillis()

    fun checkAndReset(time: Long): Boolean {
        if(!hasPassed(time)) return false
        resetTime = System.currentTimeMillis()
        return true
    }

    fun hasPassed(time: Long): Boolean {
        return System.currentTimeMillis() - time > resetTime
    }

}