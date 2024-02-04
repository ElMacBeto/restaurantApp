package com.humbjorch.restaurantapp.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.core.di.ModuleSharePreference
import com.humbjorch.restaurantapp.data.datasource.remote.Resource
import com.humbjorch.restaurantapp.domain.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val sharePreference: ModuleSharePreference
): ViewModel() {

    private var _updateOrderLiveData = MutableLiveData<Resource<Boolean>>()
    val updateOrderLiveData: LiveData<Resource<Boolean>> get() = _updateOrderLiveData

    fun savePrinter(printerAddress: String, printerPort: String){
        viewModelScope.launch {
            sharePreference.savePrinterAddress(printerAddress, printerPort.toInt())
            App.printerAddress = printerAddress
            App.printerPort = printerPort.toInt()
        }
    }

    fun cleanTableAvailable(){
        _updateOrderLiveData.value = Resource.loading()
        viewModelScope.launch {
            App.tablesAvailable.onEach {
                it.available = true
            }
            _updateOrderLiveData.value = productsRepository.updateTables()
        }
    }

    fun getPrinterPort() = sharePreference.getPrinterPort()

    fun getPrinterAddress() = sharePreference.getPrinterAddress()
}