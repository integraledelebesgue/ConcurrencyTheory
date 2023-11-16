import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Buffer(private val maxValue: Int, private val timeout: Long) {
    private val lock: Lock = ReentrantLock()

    private var firstConsumerWaiting = false
    private val firstConsumer = lock.newCondition()
    private val restConsumer = lock.newCondition()

    private var firstProducerWaiting = false
    private val firstProducer = lock.newCondition()
    private val restProducer = lock.newCondition()

    private var currentValue = 0

    @Throws(InterruptedException::class)
    fun put(amount: Int): Boolean {
        lock.withLock {
            while (firstProducerWaiting)
                if (!restProducer.await(timeout))
                    return false

            firstProducerWaiting = true

            while (currentValue + amount > maxValue)
                if (!firstProducer.await(timeout)) {
                    firstProducerWaiting = false
                    return false
                }

            currentValue += amount

            firstProducerWaiting = false

            firstConsumer.signal()
            restProducer.signal()

            return true
        }
    }

    @Throws(InterruptedException::class)
    fun get(amount: Int): Boolean {
        lock.withLock {
            while (firstConsumerWaiting)
                if (!restConsumer.await(timeout))
                    return false

            firstConsumerWaiting = true

            while (currentValue < amount)
                if (!firstConsumer.await(timeout)) {
                    firstConsumerWaiting = false
                    return false
                }

            currentValue -= amount

            firstConsumerWaiting = false

            firstProducer.signal()
            restConsumer.signal()

            return true
        }
    }

    private fun Condition.await(timeout: Long): Boolean {
        return await(timeout, TimeUnit.MILLISECONDS)
    }

    override fun toString(): String {
        return currentValue.toString()
    }
}
