import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.reflect.KClass

class Parameters(private val type: KClass<out Worker>, maxAmount: Int, val lifetime: Long) {
    private val id = nextId()
    val amount = generator.nextInt().absoluteValue % maxAmount + 1
    private val code = typeCode[type]!!

    private fun nextId(): Int {
        instanceCount += 1
        return instanceCount
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun dump(): String {
        return "$code;$id;$amount"
    }

    override fun toString(): String {
        return "${type.simpleName} (id: $id, amount: $amount)"
    }

    companion object {
        private const val seed = 2137
        private val generator = Random(seed)
        private var instanceCount = 0

        private val typeCode = mapOf(
            Producer::class to 0,
            Consumer::class to 1
        )
    }
}
