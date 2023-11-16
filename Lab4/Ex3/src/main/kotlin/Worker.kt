import java.util.concurrent.Callable
import kotlin.system.measureTimeMillis

data class Result(val actions: Int, val avgTime: Double) {
    private val avgTimeRepresentation = "%.2f".format(avgTime)

    fun dump(): String {
        return "$actions;$avgTime"
    }

    override fun toString(): String {
        return "ran $actions times waiting $avgTimeRepresentation ms averagely"
    }
}

abstract class Worker(protected val buffer: Buffer, parameters: Parameters) : Callable<Result> {
    private var start: Long = 0
    protected var actions = 0
    protected val amount: Int = parameters.amount
    private val lifetime: Long = parameters.lifetime

    private var times = mutableListOf<Long>()

    override fun call(): Result {
        start = System.currentTimeMillis()

        while (alive())
            try {
                val time = measureTimeMillis { action() }
                times.add(time)
            } catch (ignore: InterruptedException) {}

        return Result(
            actions,
            times.average()
        )
    }

    private fun alive(): Boolean {
        return System.currentTimeMillis() - start < lifetime
    }

    @Throws(InterruptedException::class)
    protected abstract fun action()
}
