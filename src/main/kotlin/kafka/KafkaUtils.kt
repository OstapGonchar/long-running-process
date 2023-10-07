package kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import request.Request
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class KafkaUtils {

    fun getProducer(consumerGroup: String): KafkaProducer<String, Request> {
        val configPath = javaClass.classLoader.getResource("producer.properties").path
        val producerProperties = loadConfig(configPath)
        producerProperties[ConsumerConfig.GROUP_ID_CONFIG] = consumerGroup
        return KafkaProducer<String, Request>(producerProperties)
    }

    fun getConsumer(
        consumerGroup: String,
        pollRecords: Int = 1,
        enableAutoCommit: Boolean = true
    ): KafkaConsumer<String, Request> {
        val configPath = javaClass.classLoader.getResource("consumer.properties").path
        val consumerProperties = loadConfig(configPath)
        consumerProperties[ConsumerConfig.GROUP_ID_CONFIG] = consumerGroup
        consumerProperties["value.deserializer.type"] = Request::class.java
        consumerProperties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        consumerProperties[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = pollRecords
        consumerProperties[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = enableAutoCommit
        return KafkaConsumer<String, Request>(consumerProperties)
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