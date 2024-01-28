package com.humbjorch.restaurantapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.core.di.ModuleSharePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sharePreference: ModuleSharePreference
): ViewModel() {


    fun savePrinter(printerAddress: String, printerPort: String){
        viewModelScope.launch {
            sharePreference.savePrinterAddress(printerAddress, printerPort.toInt())
            App.printerAddress = printerAddress
            App.printerPort = printerPort.toInt()
        }
    }

    fun getPrinterPort() = sharePreference.getPrinterPort()

    fun getPrinterAddress() = sharePreference.getPrinterAddress()
}