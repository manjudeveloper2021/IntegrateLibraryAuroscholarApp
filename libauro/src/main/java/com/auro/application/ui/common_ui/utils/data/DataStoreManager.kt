package com.auro.application.ui.common_ui.utils.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import androidx.datastore.preferences.core.stringPreferencesKey

class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {
    val TOKEN = stringPreferencesKey("token")

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(context.packageName)

    private suspend fun <T> DataStore<Preferences>.getFromLocalStorage(
        PreferencesKey: Preferences.Key<T>, func: T.() -> Unit) {
        data.catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map {
            it[PreferencesKey]
        }.collect {
            it?.let { func.invoke(it as T) }
        }
    }

    suspend fun <T> storeValue(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit {
            it[key] = value
        }
    }

    suspend fun <T> readValue(key: Preferences.Key<T>, responseFunc: T.() -> Unit) {
        context.dataStore.getFromLocalStorage(key) {
            responseFunc.invoke(this)
        }
    }


}