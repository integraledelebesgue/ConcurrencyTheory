import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.math.pow

class Benchmark(private val nThreadsValues: IntArray, private val nPixels: Int, private val nTrials: Int) {
    private val result: Map<Pair<Int, Int>, Pair<Double, Double>> by lazy {
        parameterGrid(nThreadsValues)
            .associateWith { (nThreads, chunkSize) -> test(nThreads, chunkSize, nTrials) }
            .mapKeys { (key, _) -> Pair(key.first, nPixels / key.second) }
    }

    fun show() {
        result.forEach {
            entry -> println(entry)
        }
    }

    private val json = Json { allowStructuredMapKeys = true }

    fun save(destination: String) {
        val dump = json.encodeToString(result)

        File(destination).bufferedWriter().use {
            it.write(dump)
        }
    }

    private fun test(nThreads: Int, chunkSize: Int, nTrials: Int): Pair<Double, Double> {
        val results = (1..nTrials).map {
            Mandelbrot.benchmark(nThreads = nThreads, chunkSize = chunkSize).inWholeNanoseconds
        }

        return Pair(
            results.average(),
            results.std()
        )
    }

    private fun chunkSizes(nThreads: Int): List<Pair<Int, Int>> {
        return listOf(
            Pair(nThreads, 1),
            Pair(nThreads, nPixels / nThreads),
            Pair(nThreads, nPixels / (nThreads * 10))
        )
    }

    private fun parameterGrid(nThreads: IntArray): List<Pair<Int, Int>> {
        return nThreads.flatMap { n -> chunkSizes(n) }
    }
}

private fun List<Long>.std(): Double {
    val avg = this.average()
    return this.sumOf { element -> (element - avg).pow(2) }
        .sqrt()
        .div(this.size - 1)
}

private fun Double.sqrt(): Double {
    return kotlin.math.sqrt(this)
}
