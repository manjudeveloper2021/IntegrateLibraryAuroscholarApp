package com.auro.application.core.platform

import android.os.Build
import androidx.navigation.NavType
import com.google.gson.Gson
import android.os.Bundle
import android.os.Parcelable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class GenericNavType<T>(private val clazz: Class<T>) : NavType<T>(isNullableAllowed = false) {
    private val gson = Gson()

    override fun get(bundle: Bundle, key: String): T? {
        return bundle.getString(key)?.let {
            gson.fromJson(it, clazz)
        }
    }

    override fun parseValue(value: String): T {
        return gson.fromJson(value, clazz)
    }

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putString(key, gson.toJson(value))
    }
}

class CustomNavType<T : Parcelable>(
    private val clazz: Class<T>,
    private val serializer: KSerializer<T>,
) : NavType<T>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): T? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, clazz) as T
        } else {
            @Suppress("DEPRECATION") // for backwards compatibility
            bundle.getParcelable(key)
        }

    override fun parseValue(value: String): T = Json.decodeFromString(serializer, value)

    override fun put(bundle: Bundle, key: String, value: T) =
        bundle.putParcelable(key, value)

    override fun serializeAsValue(value: T): String = Json.encodeToString(serializer, value)

    override val name: String = clazz.name
}