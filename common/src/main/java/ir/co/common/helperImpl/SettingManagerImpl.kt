package ir.co.common.helperImpl

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.co.common.dto.MovieInfoResponse
import ir.co.common.dto.MoviesResponse
import ir.co.common.helper.SecurityHelper
import ir.co.common.helper.SettingManager


class SettingManagerImpl(
    private val preferences: SharedPreferences,
    private val securityHelper: SecurityHelper
) : SettingManager {
    // generate from > https://www.guidgen.com/
    companion object {
        const val MOVIES_KEY = "7e2afccc-5f3e-4cf0-91e9-c4b2cfda980c"
        const val MOVIE_INFO_KEY = "8c2d67ca-8343-470d-8fe3-9bb16b7617f9"
    }

    private val intValues: HashMap<String, Int> = hashMapOf()
    private val longValues: HashMap<String, Long> = hashMapOf()
    private val stringValues: HashMap<String, String> = hashMapOf()
    private val booleanValues: HashMap<String, Boolean> = hashMapOf()
    private val intListValues: HashMap<String, List<Int>> = hashMapOf()


    override fun setMovies(hashMap: HashMap<Int, MoviesResponse>) {
        put(MOVIES_KEY, Gson().toJson(hashMap))
    }

    override fun getMovies(): HashMap<Int, MoviesResponse>? =
        Gson().fromJson(
            getString(MOVIES_KEY),
            object : TypeToken<HashMap<Int, MoviesResponse>?>() {}.type
        )

    override fun setMovieInfo(movie: MovieInfoResponse) {
        var hashMap: HashMap<String, MovieInfoResponse>? = getMovieInfoHashMap()
        if (hashMap.isNullOrEmpty()) hashMap = hashMapOf()
        hashMap[movie.imdbID] = movie
        put(MOVIE_INFO_KEY, Gson().toJson(hashMap))
    }

    override fun getMovieInfo(movieId: String): MovieInfoResponse? {
        val data = getMovieInfoHashMap()
        return if (data == null) null
        else data[movieId]
    }


    private fun getMovieInfoHashMap(): HashMap<String, MovieInfoResponse>? = Gson().fromJson(
        getString(MOVIE_INFO_KEY),
        object : TypeToken<HashMap<String, MovieInfoResponse>?>() {}.type
    )

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