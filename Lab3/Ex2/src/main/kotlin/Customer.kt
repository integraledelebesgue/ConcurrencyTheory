import kotlin.math.absoluteValue
import kotlin.random.Random

class Customer(private val waiter: Waiter) : Runnable {
    private val id = nextPairId()

    private val startupTime
        get() = generator
            .nextLong()
            .absoluteValue
            .mod(500L)
            .plus(500)

    override fun run() {
        try {
            Thread.sleep(startupTime)

            println("Booking table for  $id")
            waiter.acquire(id)

            Thread.sleep(diningTime)

            waiter.release()
            println("Finished  $id")
        } catch (ignore: InterruptedException) {}
    }

    companion object {
        private const val seed = 2137
        private const val diningTime = 400L
        private val generator = Random(seed)

        private const val nPairs: Int = Main.nPairs

        private var instanceCount = -1

        private fun nextPairId(): Int {
            instanceCount += 1
            return instanceCount % nPairs
        }
    }
}
