import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach

sealed class Worker<T>(protected open val transform: (T) -> T) {
    abstract val id: Long

    protected abstract suspend fun action()
    protected abstract fun log(current: T)

    protected lateinit var input: Channel<T>
    private val output: Channel<T> by lazy { Channel() }

    suspend fun run() {
        println(name() + " started")

        action()

        if (this::input.isInitialized)
            input.close()

        output.close()
    }

    fun pipeInto(other: Worker<T>) {
        other.input = output
    }

    protected fun T.transform(): T {
        return transform(this)
    }

    protected suspend fun T.send() {
        output.send(this)
    }

    fun name(): String {
        val className = this::class.simpleName
        return "$className $id"
    }

    class Producer<T>(private val provider: Sequence<T>, override val transform: (T) -> T): Worker<T>(transform) {
        override val id: Long = next()

        override suspend fun action() {
            provider.forEach { e ->
                log(e)
                e.transform().send()
            }
        }

        override fun log(current: T) {
            println(name() + " produced item " + current)
        }

        companion object: Counter()
    }

    class Consumer<T>(transform: (T) -> Unit): Worker<T>(transform as (T) -> T) {
        override val id: Long = next()

        override suspend fun action() {
            input.consumeEach { e ->
                log(e)
                e.transform()
            }
        }

        override fun log(current: T) {
            println(name() + " consumed item " + current)
        }

        companion object: Counter()
    }

    class Processor<T>(override val transform: (T) -> T): Worker<T>(transform) {
        override val id: Long = next()

        override suspend fun action() {
            input.consumeEach { e ->
                log(e)
                e.transform().send()
            }
        }

        override fun log(current: T) {
            println(name() + " processed item " + current)
        }

        companion object: Counter()
    }
}