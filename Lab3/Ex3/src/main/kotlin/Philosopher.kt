import java.util.concurrent.Semaphore
import kotlin.math.absoluteValue
import kotlin.random.Random

class Philosopher(
    private val id: Int,
    private val leftFork: Fork,
    private val rightFork: Fork,
    private val maxActions: Int,
    private val naive: Boolean,
    private val allowBreaks: Boolean,
) : Thread() {
    private val time: Long
        get() = generator
            .nextLong()
            .absoluteValue
            .mod(500L)
            .plus(500)

    override fun run() {
        try {
            for (i in 1..maxActions)
                if (naive) naiveAction()
                else semaphoreAction()
        } catch (ignore: InterruptedException) {}
    }

    @Throws(InterruptedException::class)
    private fun naiveAction() {
        action()
    }

    @Throws(InterruptedException::class)
    private fun semaphoreAction() {
        semaphore.acquire()
        action()
        semaphore.release()
    }

    @Throws(InterruptedException::class)
    private fun action() {
        if (allowBreaks)
            sleep()

        acquireForks()
        sleep()
        releaseForks()
    }

    @Throws(InterruptedException::class)
    private fun sleep() {
        println("Philosopher $id takes a break")
        sleep(time)
    }

    @Throws(InterruptedException::class)
    private fun acquireForks() {
        rightFork.acquire()
        println("Philosopher $id takes the right fork (${rightFork.id})")

        leftFork.acquire()
        println("Philosopher $id takes the left fork (${leftFork.id})")
    }

    @Throws(InterruptedException::class)
    private fun releaseForks() {
        rightFork.release()
        println("Philosopher $id puts the right fork (${rightFork.id})")

        leftFork.release()
        println("Philosopher $id puts down the left fork (${leftFork.id})")

    }

    companion object {
        private const val seed = 2137
        private val generator = Random(seed)
        private val semaphore: Semaphore = Semaphore(Main.nPhilosophers - 1)
    }
}