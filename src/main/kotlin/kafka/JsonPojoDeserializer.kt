import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.common.serialization.Deserializer

class JsonPojoDeserializer<T> : Deserializer<T> {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
    private lateinit var valueType: Class<T>

    @Suppress("unchecked_cast")
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
        valueType = configs!!["value.deserializer.type"] as Class<T>
    }

    override fun deserialize(topic: String?, data: ByteArray): T {
        return objectMapper.readValue(data, valueType)
    }
}
