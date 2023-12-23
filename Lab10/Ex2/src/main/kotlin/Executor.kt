import java.lang.Thread.sleep

object Executor {
    fun examplePipeline(nProcessors: Long): Pipeline<Int> {
        val producer = Worker.Producer((1..10).asSequence()) { e -> e }
        val consumer = Worker.Consumer<Int> { e -> println(e) }
        val processors = (1..nProcessors)
            .map { i -> Worker.Processor<Int> { x ->
                sleep(100)
                x + i.toInt()
            } }
            .toTypedArray()

        return Pipeline(
            producer,
            consumer,
            *processors,
        )
    }
}