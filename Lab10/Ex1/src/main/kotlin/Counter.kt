open class Counter {
    private var state = 0L

    val next: Long
        get() {
            return state++
        }
}