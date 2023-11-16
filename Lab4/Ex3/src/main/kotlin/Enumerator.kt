open class Enumerator {
    private var instanceCount = 0

    fun nextId(): Int {
        instanceCount += 1
        return instanceCount
    }
}