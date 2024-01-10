import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.selects.select

sealed class Worker<T>(
    protected val transform: (T) -> T,
    protected open vararg val channels: Channel<T>
) {
    protected abstract val id: Long

    protected abstract suspend fun action()
    protected abstract fun T.log()

    val name: String
        get() = "${this::class.simpleName} $id"

    suspend fun run() {
        println("$name started")
        action()
    }

    protected fun T.transform(): T {
        return transform(this)
    }

    class Producer<T : Any>(
        private val source: Flow<T>,
        transform: (T) -> T,
        override vararg val channels: Channel<T>
    ) : Worker<T>(transform, *channels) {
        override val id: Long = next

        override suspend fun action() {
            source.collect {
                it.log()
                it.transform().send()
            }

            channels.forEach { it.close() }
        }

        private suspend fun T.send() {
            select {
                channels.forEach { channel ->
                    channel.onSend(this@send) {}
                }
            }
        }

        override fun T.log() {
            println("$name produced item $this")
        }

        companion object : Counter()
    }

    class Consumer<T>(
        transform: (T) -> Unit,
        override vararg val channels: Channel<T>
    ) : Worker<T>(
        transform as (T) -> T,
        *channels
    ) {
        override val id: Long = next

        override suspend fun action() {
            while (true) {
                receive()?.apply {
                    this.log()
                    this.transform()
                } ?: break
            }
        }

        private suspend fun receive(): T? {
            return select {
                channels.forEach { channel ->
                    channel.onReceiveCatching { value ->
                        return@onReceiveCatching value.getOrNull()
                    }
                }
            }
        }

        override fun T.log() {
            println("$name consumed item $this")
        }

        companion object : Counter()
    }
}