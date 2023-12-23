open class Counter {
    private var state = 0L

    fun next(): Long {
        state += 1
        return state
    }
}