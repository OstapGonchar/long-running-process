package kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.common.serialization.Serializer

class JsonPojoSerializer<T> : Serializer<T> {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    override fun serialize(topic: String?, data: T): ByteArray {
        return objectMapper.writeValueAsBytes(data)
    }
}
