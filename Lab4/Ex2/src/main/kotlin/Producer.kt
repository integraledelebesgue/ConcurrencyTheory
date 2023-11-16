import kotlin.math.absoluteValue
import kotlin.random.Random

class Producer(private val buffer: Buffer, private val maxAmount: Int, private val actions: Int, seed: Long): Thread() {
    private val generator: Random = Random(seed)
    private val threadId: Long by lazy { currentThread().threadId() }

    private val randomAmount
        get() = generator
            .nextInt()
            .absoluteValue
            .mod(maxAmount)
            .plus(1)

    override fun run() {
        for (i in 1..actions)
            action()

        logEnd()
    }

    private fun action() {
        buffer.lock()

        val amount = randomAmount

        while (buffer.availableSpace < amount)
            buffer.producers.await()

        buffer.put(amount)

        this.log(amount)
        buffer.log()

        buffer.consumers.signal()

        buffer.unlock()
    }

    private fun logEnd() {
        println("\tProducer $threadId finished work")
    }

    private fun log(amount: Int) {
        println("Producer $threadId puts $amount items")
    }
}