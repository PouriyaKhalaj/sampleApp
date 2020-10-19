package ir.co.common.helperImpl

import android.content.SharedPreferences
import ir.co.common.helper.SecurityHelper
import ir.co.common.helper.SettingManager


class SettingManagerImpl(
    private val preferences: SharedPreferences,
    private val securityHelper: SecurityHelper
) : SettingManager {
    // generate from > https://www.guidgen.com/
    companion object {
        const val MOVIES_KEY = "7e2afccc-5f3e-4cf0-91e9-c4b2cfda980c"
    }

    private val intValues: HashMap<String, Int> = hashMapOf()
    private val longValues: HashMap<String, Long> = hashMapOf()
    private val stringValues: HashMap<String, String> = hashMapOf()
    private val booleanValues: HashMap<String, Boolean> = hashMapOf()
    private val intListValues: HashMap<String, List<Int>> = hashMapOf()

    //****************************************************************************************//




    //****************************************************************************************//

    private fun put(key: String, value: String) {
        synchronized(this) {
            val encrypted = securityHelper.encrypt(value)
            stringValues[key] = encrypted
            preferences.edit().putString(key, encrypted).apply()
        }
    }

    private fun putRaw(key: String, value: String) {
        synchronized(this) {
            stringValues[key] = value
            preferences.edit().putString(key, value).apply()
        }
    }

    private fun put(key: String, value: Boolean) {
        synchronized(this) {
            booleanValues[key] = value
            preferences.edit().putBoolean(key, value).apply()
        }
    }

    private fun put(key: String, value: Int) {
        synchronized(this) {
            intValues[key] = value
            preferences.edit().putInt(key, value).apply()
        }
    }

    private fun put(key: String, value: Long) {
        synchronized(this) {
            longValues[key] = value
            preferences.edit().putLong(key, value).apply()
        }
    }

    private fun getString(key: String): String {
        synchronized(this) {
            val value = if (stringValues.containsKey(key))
                stringValues[key] ?: ""
            else
                preferences.getString(key, "") ?: ""
            return securityHelper.decrypt(value)
        }
    }

    private fun getInt(key: String): Int {
        synchronized(this) {
            return if (intValues.containsKey(key))
                intValues[key] ?: 0
            else
                preferences.getInt(key, 0)
        }
    }

    private fun getLong(key: String): Long {
        synchronized(this) {
            return if (longValues.containsKey(key))
                longValues[key] ?: 0
            else
                preferences.getLong(key, 0)
        }
    }

    private fun getRawString(key: String): String {
        synchronized(this) {
            return if (stringValues.containsKey(key))
                stringValues[key] ?: ""
            else
                preferences.getString(key, "") ?: ""
        }
    }

    private fun getBoolean(key: String): Boolean {
        synchronized(this) {
            return if (booleanValues.containsKey(key))
                booleanValues[key] ?: false
            else
                preferences.getBoolean(key, false)
        }
    }

    private fun getIntList(key: String): List<Int> {
        synchronized(this) {
            return if (intListValues.containsKey(key))
                intListValues[key] ?: emptyList()
            else
                intListValues.get(key)!!
        }
    }

}