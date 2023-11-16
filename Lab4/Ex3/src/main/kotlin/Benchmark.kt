import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

object Benchmark {
    private const val workerLifetime: Long = 200000

    private const val maxBufferValue = 1000
    private const val bufferTimeout = workerLifetime

    private const val consumerCount = 100
    private const val maxProducerAmount = maxBufferValue / 2

    private const val producerCount = 100
    private const val maxConsumerAmount = maxBufferValue / 2

    private val buffer = Buffer(maxBufferValue, bufferTimeout)

    private const val dataDestination = "data/benchmark.csv"
    private const val dataSchema = "type;id;amount;nActions;avgTime\n"
    private const val save = true
    private const val overwrite = true

    @JvmStatic
    operator fun invoke() {
        val producerAverages: Map<Parameters, Future<Result>>
        val consumerAverages: Map<Parameters, Future<Result>>

        Executors.newFixedThreadPool(consumerCount).use { consumers ->
            Executors.newFixedThreadPool(producerCount).use { producers ->
                producerAverages = spawnProducers(producers)
                consumerAverages = spawnConsumers(consumers)
            }
        }

        if (save && overwrite)
            prepareFile()

        producerAverages.awaitAndProcess()
        consumerAverages.awaitAndProcess()
    }

    private fun prepareFile() {
        File(dataDestination).writeText(dataSchema)
    }

    private fun Map<Parameters, Result>.save() {
        val text = this
            .map { (parameters, result) -> parameters.dump() + ";" + result.dump()}
            .joinToString(separator = "\n", postfix = "\n")
        
        File(dataDestination).appendText(text)
    }

    private fun Map<Parameters, Result>.display() {
        this.forEach { (parameters, result) ->
            println("$parameters $result")
        }
    }

    private fun Map<Parameters, Future<Result>>.await(): Map<Parameters, Result> {
        return this.mapValues { (_, future) -> future.get() }
    }

    private fun Map<Parameters, Future<Result>>.awaitAndProcess() {
        val result = this.await()

        result.display()

        if (save)
            result.save()
    }

    private fun spawnProducers(threadPool: ExecutorService): Map<Parameters, Future<Result>> {
        return (1..producerCount)
            .map {_ -> Parameters(Producer::class, maxProducerAmount, workerLifetime)}
            .associateWith { parameters -> threadPool.submit(Producer(buffer, parameters)) }
    }

    private fun spawnConsumers(threadPool: ExecutorService): Map<Parameters, Future<Result>> {
        return (1..consumerCount)
            .map {_ -> Parameters(Consumer::class, maxConsumerAmount, workerLifetime)}
            .associateWith { parameters -> threadPool.submit(Consumer(buffer, parameters)) }
    }
}

