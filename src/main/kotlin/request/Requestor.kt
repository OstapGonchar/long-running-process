import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.atomic.AtomicLong


class Requestor {
    private var kafkaProducer: KafkaProducer<String, Request>
    private var requestId = AtomicLong(1)

    init {
        val configPath = javaClass.classLoader.getResource("producer.properties").path
        val producerProperties = loadConfig(configPath)
        kafkaProducer = KafkaProducer<String, Request>(producerProperties)
    }

    fun sendRequest() {
        val currentId = requestId.getAndIncrement()
        println("Sending request for ID: $currentId")
        kafkaProducer.send(
            ProducerRecord(
                "requests", "request-key-${currentId}", Request(currentId, "request-${currentId}", "INITIATED")
            )
        )
    }

    private fun loadConfig(configFile: String): Properties {
        if (!Files.exists(Paths.get(configFile))) {
            throw IOException("$configFile not found.")
        }
        val cfg = Properties()
        FileInputStream(configFile).use { inputStream -> cfg.load(inputStream) }
        return cfg
    }
}