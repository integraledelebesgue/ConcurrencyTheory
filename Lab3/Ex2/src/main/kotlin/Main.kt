import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object Main {
    const val nPairs = 10
    private const val nCustomers = 2 * nPairs

    @JvmStatic
    fun main(args: Array<String>) {
        val waiter = Waiter(nCustomers)

        Executors.newFixedThreadPool(nCustomers).use {
            spawnCustomers(waiter, it)
        }
    }

    private fun spawnCustomers(waiter: Waiter, threadPool: ExecutorService) {
        for (i in 1..nCustomers)
            threadPool.execute(Customer(waiter))
    }
}