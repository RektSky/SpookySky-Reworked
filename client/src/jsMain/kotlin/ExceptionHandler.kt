fun Throwable.printJSStackTrace() {
    val stack = this.asDynamic().stack
    if (stack is String) {
        val error = js("Error()")
        error.name = this.toString().substringBefore(':')
        error.message = this.message?.substringAfter(':')
        error.stack = stack
        console.error(error)
    } else {
        console.log(this)
    }
}