import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

const val terminationTimeout: Long = 1

const val seed: Long = 2137

const val numProducers = 4
const val numProducerActions = 10
const val maxProducerWorkAmount = 6

const val numConsumers = 6
const val numConsumerActions = 10
const val maxConsumerWorkAmount = 5

const val bufferSize = 10

fun main() {
    val generator = Random(seed)
    val buffer = Buffer(bufferSize)

    val threadPool: ExecutorService = Executors.newFixedThreadPool(numProducers + numConsumers)

    spawnProducers(buffer, threadPool, generator, numProducers, maxProducerWorkAmount, numProducerActions)
    spawnConsumers(buffer, threadPool, generator, numConsumers, maxConsumerWorkAmount, numConsumerActions)

    threadPool.awaitTermination(terminationTimeout, TimeUnit.SECONDS)
}

fun spawnConsumers(buffer: Buffer, threadPool: ExecutorService, generator: Random, number: Int, maxWorkAmount: Int, numActions: Int) {
    for (i in 1..number)
        threadPool.execute(Consumer(
            buffer,
            maxWorkAmount,
            numActions,
            generator.nextLong()
        ))
}

fun spawnProducers(buffer: Buffer, threadPool: ExecutorService, generator: Random, number: Int, maxWorkAmount: Int, numActions: Int) {
    for (i in 1..number)
        threadPool.execute(Producer(
            buffer,
            maxWorkAmount,
            numActions,
            generator.nextLong()
        ))
}

