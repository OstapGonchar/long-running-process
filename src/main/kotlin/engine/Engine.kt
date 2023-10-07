package engine

import kafka.KafkaUtils
import org.apache.kafka.clients.producer.ProducerRecord
import java.time.Duration
import kotlin.random.Random

class Engine(engineNumber: Int, private val sleepSeconds: Int) {
    private val kafkaConsumer = KafkaUtils().getConsumer("engine", enableAutoCommit = false)
    private val kafkaProducer = KafkaUtils().getProducer("engine-$engineNumber")

    fun start() {
        while (true) {
            processMessages()
        }

    }

    /**
     * Concept to scale engines here is the following:
     * * All engines work on same the consumer group on **requests** topic
     * * Each engine tries to subscribe to **requests** topic under same consumer group, only one can succeed
     * * It picks one request, pushes status to **status** topic and immediately unsubscribes from **requests** topic
     * * Then it starts slow processing an updates status on **status** topic
     *
     * This way we can scale engines depending on consumer lag on **requests** topic.
     * In case engine fails to process and for some reason also fails to update **status** topic
     * -> the request would be stuck in IN_PROGRESS topic.
     *
     * If engine failed for some reason sending even IN_PROGRESS,
     * then it would not even commit polled record and other engine would pick it up
     */
    private fun processMessages() {
        kafkaConsumer.subscribe(listOf("requests"))
        val records = kafkaConsumer.poll(Duration.ofMillis(200))
        records.forEach {
            var request = it.value()
            val currentId = request.id
            println("Picked up request #$currentId")

            println("Processing request #$currentId slowly")
            request = request.copy(status = "IN_PROGRESS")
            kafkaProducer.send(ProducerRecord("status", "request-key-${currentId}", request))

            // Main concept here
            kafkaProducer.flush()
            kafkaConsumer.commitSync()
            kafkaConsumer.unsubscribe()

            slowProcessing()

            val resultedStatus = if (Random.nextBoolean()) "SUCCESS" else "FAILED"
            request = request.copy(status = resultedStatus)
            println("Finished processing request #$currentId")
            kafkaProducer.send(ProducerRecord("status", "request-key-${currentId}", request))
        }
    }

    private fun slowProcessing() {
        Thread.sleep(sleepSeconds.toLong() * 1000)
    }
}