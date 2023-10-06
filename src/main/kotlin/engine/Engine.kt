package engine

import kafka.KafkaUtils
import org.apache.kafka.clients.producer.ProducerRecord
import java.time.Duration
import kotlin.random.Random

class Engine(engineNumber: Int, private val sleepSeconds: Int) {
    private val kafkaConsumer = KafkaUtils().getConsumer("engine-$engineNumber")
    private val kafkaProducer = KafkaUtils().getProducer("engine-$engineNumber")

    fun start() {
        kafkaConsumer.subscribe(listOf("requests"))
        while (true) {
            processMessages()
        }

    }

    private fun processMessages() {
        val records = kafkaConsumer.poll(Duration.ofMillis(200))
        records.forEach {
            var request = it.value()
            val currentId = request.id
            println("Picked up request #$currentId")

            println("Processing request #$currentId slowly")
            request = request.copy(status = "IN_PROGRESS")
            kafkaProducer.send(ProducerRecord("status", "request-key-${currentId}", request))

            Thread.sleep(sleepSeconds.toLong() * 1000)
            val resultedStatus = if (Random.nextBoolean()) "SUCCESS" else "FAILED"
            request = request.copy(status = resultedStatus)
            println("Finished processing request #$currentId")
            kafkaProducer.send(ProducerRecord("status", "request-key-${currentId}", request))
        }
    }
}