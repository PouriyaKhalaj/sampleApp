package ir.co.common.helper

interface SerializationHelper {

    fun serialize(any: Any): String

    fun <T> deserialize(jsonString: String, cls: Class<T>): T

}