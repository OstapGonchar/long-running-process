package status

import kafka.KafkaUtils
import org.apache.kafka.common.TopicPartition
import request.Request
import java.time.Duration
import kotlin.concurrent.thread

class StatusObserver {
    private val requestMap: MutableMap<Long, Request> = mutableMapOf()

    init {
        thread(start = true) {
            val kafkaConsumer = KafkaUtils().getConsumer("status-observer", 10)

            val topicPartition = TopicPartition("status", 0)
            kafkaConsumer.assign(listOf(topicPartition))
            kafkaConsumer.seek(topicPartition, 0)

            while (true) {
                val records = kafkaConsumer.poll(Duration.ofMillis(100))
                records.map { it.value() }.forEach { requestMap[it.id] = it }
            }
        }
    }

    fun printCurrentStatus() {
        println("========================")
        requestMap.values
            .map { "Request #${it.id} has status ${it.status}" }
            .joinToString("\n").run { println(this) }
        println("========================")
    }

}