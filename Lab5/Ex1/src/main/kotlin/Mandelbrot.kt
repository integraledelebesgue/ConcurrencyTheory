import java.awt.Graphics
import java.awt.image.BufferedImage
import java.lang.Exception
import java.util.concurrent.*
import javax.swing.JFrame
import kotlin.math.pow
import kotlin.properties.Delegates
import kotlin.time.Duration
import kotlin.time.measureTime

class Mandelbrot(
    private val maxIter: Int,
    private val zoom: Double,
    private val convergenceThreshold: Double,
    private val nThreads: Int,
    private val chunkSize: Int,
    width: Int,
    height: Int,
    title: String = "Mandelbrot Set"
) : JFrame(title) {
    private val image: BufferedImage by lazy { BufferedImage(width, height, BufferedImage.TYPE_INT_RGB) }
    private var time: Duration by Delegates.notNull()

    init {
        setup(width, height)
        computePixels()?.fillImage()
    }

    private fun setup(width: Int, height: Int) {
        setBounds(100, 100, width, height)
        setResizable(false)
        setDefaultCloseOperation(EXIT_ON_CLOSE)
    }

    private fun toPoint(x: Int, y: Int): Pair<Double, Double> {
        return Pair(x.toDouble(), y.toDouble())
    }

    private fun computePixels(): Map<Pair<Double, Double>, Int>? {
        val threadPool: ExecutorService = Executors.newFixedThreadPool(nThreads)
        val result: Map<Pair<Double, Double>, Int>

        try {
            time = measureTime { result = scheduleAndAwait(threadPool) }
            return result
        }
        catch (ignore: Exception) {}
        finally {
            threadPool.shutdown()
            threadPool.awaitTermination(1, TimeUnit.MICROSECONDS)
        }

        return null
    }

    private fun Map<Pair<Double, Double>, Int>.fillImage() {
        this.forEach { (point, color) -> point.setPixel(color) }
    }

    private fun scheduleAndAwait(threadPool: ExecutorService): Map<Pair<Double, Double>, Int> {
        return chunks()
            .map { chunk -> chunk.schedule(threadPool) }
            .flatMap { result -> result.get() }
            .toMap()
    }

    private fun points(): List<Pair<Double, Double>> {
        return (0..<width).flatMap { i -> (0..<height).map { j -> toPoint(i, j) } }
    }

    private fun chunks(): List<List<Pair<Double, Double>>> {
        return points().shuffled().chunked(chunkSize)
    }

    private fun List<Pair<Double, Double>>.schedule(threadPool: ExecutorService): Future<List<Pair<Pair<Double, Double>, Int>>> {
        return threadPool.submit(Callable {
            this.map {point -> Pair(point, point.shift().limit())}
        })
    }

    private fun Pair<Double, Double>.setPixel(value: Int) {
        image.setRGB(first.toInt(), second.toInt(), value)
    }

    private fun Pair<Double, Double>.limit(): Int {
        var z = Pair(0.0, 0.0)

        for (i in maxIter downTo 1) {
            z = z.step(this)

            if (z.divergent())
                return i.toColor()
        }

        return 0
    }

    private fun Int.toColor(): Int {
        return this or (this shl 8)
    }

    private fun Pair<Double, Double>.step(start: Pair<Double, Double>): Pair<Double, Double> {
        return this.complexSquare() + start
    }

    private fun Pair<Double, Double>.shift(): Pair<Double, Double> {
        return Pair(
            (first - width / 2) / zoom,
            (second - height / 2) / zoom
        )
    }

    private fun Pair<Double, Double>.divergent(): Boolean {
        return this.l2Norm() > convergenceThreshold
    }

    override fun paint(g: Graphics) {
        g.drawImage(image, 0, 0, this)
    }

    companion object {
        @JvmStatic
        fun main(
            maxIter: Int = 570,
            zoom: Double = 190.0,
            threshold: Double = 4.0,
            nThreads: Int = 16,
            chunkSize: Int = 1,
            width: Int = 800,
            height: Int = 600
        ) {
            val app = Mandelbrot(maxIter, zoom, threshold, nThreads, chunkSize, width, height)
            app.isVisible = true
        }

        @JvmStatic
        fun benchmark(
            maxIter: Int = 570,
            zoom: Double = 190.0,
            threshold: Double = 4.0,
            nThreads: Int = 16,
            chunkSize: Int = 1,
            width: Int = 800,
            height: Int = 600
        ): Duration {
            return Mandelbrot(maxIter, zoom, threshold, nThreads, chunkSize, width, height).time
        }
    }
}

private operator fun Pair<Double, Double>.plus(point: Pair<Double, Double>): Pair<Double, Double> {
    return Pair(
        first.plus(point.first),
        second.plus(point.second)
    )
}

private fun Pair<Double, Double>.complexSquare(): Pair<Double, Double> {
    return Pair(
        first.pow(2) - second.pow(2),
        2 * first * second
    )
}

private fun Pair<Double, Double>.l2Norm(): Double {
    return first.pow(2) + second.pow(2)
}
