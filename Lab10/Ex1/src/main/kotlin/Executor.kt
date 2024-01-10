import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object Executor {
    fun <T : Any> runProducerConsumer(
        source: Flow<T>,
        nChannels: Long,
        producerTransform: (T) -> T,
        consumerAction: (T) -> Unit
    ) {
        val channels = (1..nChannels).map { Channel<T>() }.toTypedArray()
        val producer = Worker.Producer(source, producerTransform, *channels)
        val consumer = Worker.Consumer(consumerAction, *channels)

        runBlocking {
            launch { producer.run() }
            launch { consumer.run() }
        }
    }
}