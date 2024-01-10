import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

const val nItems = 10
const val nChannels = 5L

fun slowNumbers(length: Int): Flow<Int> = flow {
    for (i in 1..length) {
        delay(500)
        emit(i)
    }
}

fun main() {
    Executor.runProducerConsumer(
        slowNumbers(nItems),
        nChannels,
        { value -> 2 * value },
        { value -> println(value) }
    )
}