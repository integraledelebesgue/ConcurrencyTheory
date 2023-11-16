import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Waiter(size: Int) {
    private val lock: Lock = ReentrantLock()
    private val reservation: Condition = lock.newCondition()

    private val pairs = Array(size) { _ -> lock.newCondition() }
    private val booked = BooleanArray(size) { _ -> false }

    private var occupiedSeats = 0

    @Throws(InterruptedException::class)
    fun acquire(identifier: Int) {
        lock.withLock {
            while (occupiedSeats > 0)
                reservation.await()

            if (!booked[identifier]) {
                booked[identifier] = true

                while (booked[identifier])
                    pairs[identifier].await()
            } else {
                booked[identifier] = false
                occupiedSeats = 2
                pairs[identifier].signal()
            }
        }
    }

    @Throws(InterruptedException::class)
    fun release() {
        lock.withLock {
            occupiedSeats -= 1

            if (occupiedSeats == 0)
                reservation.signalAll()
        }
    }
}
