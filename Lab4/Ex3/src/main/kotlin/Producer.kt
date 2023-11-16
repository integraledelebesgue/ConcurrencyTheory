class Producer(buffer: Buffer, parameters: Parameters) : Worker(buffer, parameters) {
    @Throws(InterruptedException::class)
    override fun action() {
        if (buffer.put(amount))
            actions += 1
    }
}
