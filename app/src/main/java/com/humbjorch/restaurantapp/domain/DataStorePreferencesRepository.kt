package com.humbjorch.restaurantapp.domain

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.humbjorch.restaurantapp.core.utils.Constants.ORDER_NUMBER_KEY
import com.humbjorch.restaurantapp.core.utils.Constants.PRINTER_ADDRESS_KEY
import com.humbjorch.restaurantapp.core.utils.Constants.PRINTER_PORT_KEY
import com.humbjorch.restaurantapp.core.utils.Constants.SETTING_PREFERENCE
import com.humbjorch.restaurantapp.data.datasource.remote.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class DataStorePreferencesRepository @Inject constructor(
    private val context: Context
) {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = SETTING_PREFERENCE)
    }

    suspend fun savePrinterAddress(printer: String): Resource<String> {
        return try {
            context.dataStore.edit { preference ->
                preference[stringPreferencesKey(PRINTER_ADDRESS_KEY)] = printer
            }
            Resource.success(null)
        } catch (e: IOException) {
            Resource.error(msg = e.message)
        }
    }

    fun getPrinterAddress(): Flow<String> =
        context.dataStore.data.map { preference ->
            preference[stringPreferencesKey(PRINTER_ADDRESS_KEY)].orEmpty()
        }

    suspend fun savePrinterPort(printerPort: Int): Resource<Unit> {
        return try {
            context.dataStore.edit { preference ->
                preference[intPreferencesKey(PRINTER_PORT_KEY)] = printerPort
            }
            Resource.success(null)
        } catch (e: IOException) {
            Resource.error(msg = e.message)
        }
    }

    fun getPrinterPort(): Flow<Int> =
        context.dataStore.data.map { preference ->
            preference[intPreferencesKey(PRINTER_PORT_KEY)] ?: 9100
        }

    suspend fun saveOrderNumber(newOrderNumber: Int): Resource<Unit> {
        return try {
            context.dataStore.edit { preference ->
                preference[intPreferencesKey(ORDER_NUMBER_KEY)] = newOrderNumber
            }
            Resource.success(Unit)
        } catch (e: IOException) {
            Resource.error(msg = e.message)
        }
    }

    fun getOrderNumber(): Flow<Int> =
        context.dataStore.data.map { preference ->
            preference[intPreferencesKey(ORDER_NUMBER_KEY)] ?: 0
        }

}