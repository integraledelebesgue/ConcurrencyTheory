class Consumer(buffer: Buffer, parameters: Parameters) : Worker(buffer, parameters) {
    @Throws(InterruptedException::class)
    override fun action() {
        if (buffer.get(amount))
            actions += 1
//        else  // uncomment to check when timeout happens
//            System.out.println("Timed out!");
    }
}



