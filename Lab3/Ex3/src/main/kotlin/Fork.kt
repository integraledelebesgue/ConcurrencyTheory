import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class Fork(val id: Int) {
    private val lock = ReentrantLock()
    private val queue = lock.newCondition()

    private var free = true

    @Throws(InterruptedException::class)
    fun acquire() {
        lock.withLock {
            while (!free)
                queue.await()

            free = false
        }
    }

    @Throws(InterruptedException::class)
    fun release() {
        lock.withLock {
            free = true
            queue.signal()
        }
    }
}
