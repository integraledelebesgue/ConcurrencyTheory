import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

object Main {
    private const val nProcessors = 5L

    @JvmStatic
    fun main(args: Array<String>) {
        Executor.examplePipeline(nProcessors).use { processors ->
            coroutineScope {
                repeat(5) {
                    println("\n Log:\n" + processors.workers.joinToString(separator = "\n", postfix = "\n") { w -> w.status() })
                    delay(200)
                }
            }
        }
    }
}