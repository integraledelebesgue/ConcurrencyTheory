import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object Main {
    const val nPhilosophers = 5
    private const val maxActions = 10
    private const val naive = false
    private const val breaks = true

    private val forks = Array(nPhilosophers) { i -> Fork(i) }

    private fun leftFork(i: Int): Fork {
        return forks[i]
    }

    private fun rightFork(i: Int): Fork {
        return forks[(i + 1) % nPhilosophers]
    }

    private fun spawnPhilosophers(threadPool: ExecutorService) {
        (0..<nPhilosophers)
            .map { i -> Philosopher(i, leftFork(i), rightFork(i), maxActions, naive, breaks) }
            .forEach(threadPool::execute)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        Executors.newFixedThreadPool(nPhilosophers).use {
            spawnPhilosophers(it)
        }
    }
}