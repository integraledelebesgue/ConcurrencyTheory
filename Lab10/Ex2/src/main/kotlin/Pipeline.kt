import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Pipeline<T : Any>(
    private val producer: Worker.Producer<T>,
    private val consumer: Worker.Consumer<T>,
    private vararg val processors: Worker.Processor<T>
) {
    val workers: List<Worker<T>> =
        listOf(producer) +
        listOf(*processors) +
        listOf(consumer)

    init { setup() }

    private fun setup() {
        if (processors.isEmpty()) {
            producer.pipeInto(consumer)
            return
        }

        pipeProducer()
        pipeConsumer()
        pipeProcessors()
    }

    private fun pipeProducer() {
        producer.pipeInto(processors.first())
    }

    private fun pipeConsumer() {
        processors
            .last()
            .pipeInto(consumer)
    }

    private fun pipeProcessors() {
        processors
            .iterator()
            .asSequence()
            .zipWithNext()
            .forEach { (first, second) ->
                first.pipeInto(second)
            }
    }

    fun Iterable<Worker<T>>.launchAll(scope: CoroutineScope) {
        for (worker in this)
            scope.launch { worker.run() }
    }

    inline fun use(crossinline block: suspend (p: Pipeline<out Any>) -> Unit) {
        runBlocking {
            workers.launchAll(this)
            block(this@Pipeline)
        }
    }
}