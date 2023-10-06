package request

import kafka.KafkaUtils
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.concurrent.atomic.AtomicLong

class Requestor {
    private var kafkaProducer: KafkaProducer<String, Request> = KafkaUtils().getProducer("requestor")
    private var requestId = AtomicLong(1)

    fun sendRequest() {
        val currentId = requestId.getAndIncrement()
        println("Sending request for ID: $currentId")
        val request = Request(currentId, "request-${currentId}", "INITIATED")
        kafkaProducer.send(ProducerRecord("requests", "request-key-${currentId}", request))
        println("Request is sent on request topic")
        kafkaProducer.send(ProducerRecord("status", "request-key-${currentId}", request))
        println("Request is sent on status topic")
    }
}