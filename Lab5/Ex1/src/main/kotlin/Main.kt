object Main {
    private const val nTrials = 10
    private const val width = 800
    private const val height = 600
    private const val nPixels = width * height

    private const val show = true
    private const val benchmark = false

    @JvmStatic
    fun main(args: Array<String>) {
        if (show) Mandelbrot.main()
        if (!benchmark) return

        val nThreadValues = intArrayOf(1, 8, 16)

        Benchmark(nThreadValues, nPixels, nTrials).let {
            it.show()
            it.save("data/results.json")
        }
    }
}
