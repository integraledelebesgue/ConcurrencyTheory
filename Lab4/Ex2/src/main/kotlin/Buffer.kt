import java.lang.RuntimeException
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

data class Buffer(val size: Int) {
    private val lock = ReentrantLock()
    private var state = 0

    val availableItems
        get() = state

    val availableSpace
        get() = size - state

    val producers: Condition = lock.newCondition()
    val consumers: Condition = lock.newCondition()

    fun put(amount: Int) {
        if (amount > availableSpace)
            throw RuntimeException("Not enough space in buffer - available $availableSpace, expected $amount")

        state += amount
    }

    fun get(amount: Int) {
        if (amount > state)
            throw RuntimeException("Not enough items in buffer - available $state, expected $amount")

        state -= amount
    }

    fun lock() {
        lock.lock()
    }

    fun unlock() {
        lock.unlock()
    }

    fun log() {
        println("Buffer: $state / $size")
    }
}