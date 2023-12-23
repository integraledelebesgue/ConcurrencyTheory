import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

object Main {
    private const val nProcessors = 5L

    @JvmStatic
    fun main(args: Array<String>) {
        Executor.examplePipeline(nProcessors).use {
            coroutineScope {
                println("!")
                delay(100)
            }
        }
    }
}