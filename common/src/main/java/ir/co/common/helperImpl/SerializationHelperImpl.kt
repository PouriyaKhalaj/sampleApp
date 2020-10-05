package ir.co.common.helperImpl

import com.google.gson.Gson
import ir.co.common.helper.SerializationHelper


class SerializationHelperImpl(private val gson: Gson) : SerializationHelper {

    override fun serialize(any: Any): String {
        return gson.toJson(any)
    }

    override fun <T> deserialize(jsonString: String, cls: Class<T>): T {
        return gson.fromJson(jsonString, cls)
    }

}