package com.humbjorch.restaurantapp.core.di

import android.annotation.SuppressLint
import android.content.Context
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModuleSharePreference @Inject constructor(private val context: Context) {
    private val shareSessionPreferences = context.getSharedPreferences(
        context.getString(R.string.share_preferences_session),
        Context.MODE_PRIVATE
    )

    private val sharedPreferencesSessionEdit = shareSessionPreferences.edit()

    @SuppressLint("CommitPrefEdits")
    fun savePrinterAddress(printerAddress: String, printerPort: Int) {
        sharedPreferencesSessionEdit.putString(
            Constants.PRINTER_ADDRESS_KEY,
            printerAddress
        )
        sharedPreferencesSessionEdit.putInt(
           Constants.PRINTER_PORT_KEY,
            printerPort
        )
        sharedPreferencesSessionEdit.apply()
    }

    @SuppressLint("CommitPrefEdits")
    fun saveCurrentOrderNumber(newOrderNumber: Int) {
        sharedPreferencesSessionEdit.putInt(
            Constants.ORDER_NUMBER_KEY,
            newOrderNumber
        )
        sharedPreferencesSessionEdit.apply()
    }

    fun getPrinterPort() = shareSessionPreferences.getInt(
        Constants.PRINTER_PORT_KEY, 0)

    fun getPrinterAddress() = shareSessionPreferences.getString(
        Constants.PRINTER_ADDRESS_KEY, "")

    fun getOrderNumber() = shareSessionPreferences.getInt(
        Constants.ORDER_NUMBER_KEY, 0)

    @SuppressLint("CommitPrefEdits")
    fun clearPreferences() {
        sharedPreferencesSessionEdit.clear()
        sharedPreferencesSessionEdit.apply()
    }
}