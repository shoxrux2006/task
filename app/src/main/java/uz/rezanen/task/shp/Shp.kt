package uz.rezanen.task.shp

import android.content.Context

class Shp(private val context: Context) {
    private val shp = context.getSharedPreferences("shp", Context.MODE_PRIVATE)

    fun getString(name: String): String? = shp.getString(name, "")
    fun removeString(name: String) {
        shp.edit().remove(name).apply()
    }

    fun setString(name: String, data: String) {
        shp.edit().putString(name, data).apply()
    }

    fun getInt(name: String): Int? = if (shp.getInt(name, Int.MIN_VALUE) != Int.MIN_VALUE) {
        shp.getInt(name, Int.MIN_VALUE)
    } else {
        null
    }

    fun setInt(name: String, data: Int) {
        shp.edit().putInt(name, data).apply()
    }

    fun getLong(name: String): Long? = if (shp.getLong(name, Long.MIN_VALUE) != Long.MIN_VALUE) {
        shp.getLong(name, Long.MIN_VALUE)
    } else {
        null
    }

    fun setLong(name: String, data: Long) {
        shp.edit().putLong(name, data).apply()
    }

    fun getBool(name: String): Boolean = shp.getBoolean(name, true)
    fun setBool(name: String, data: Boolean) {
        shp.edit().putBoolean(name, data).apply()
    }
}