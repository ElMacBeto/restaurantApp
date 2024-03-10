package com.humbjorch.restaurantapp.ui.orderRegister.orderSection

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.core.di.ModuleSharePreference
import com.humbjorch.restaurantapp.core.utils.Tools
import com.humbjorch.restaurantapp.core.utils.printer.PrinterUtils
import com.humbjorch.restaurantapp.data.datasource.remote.Resource
import com.humbjorch.restaurantapp.data.model.OrderModel
import com.humbjorch.restaurantapp.data.model.ProductsOrderModel
import com.humbjorch.restaurantapp.domain.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderSectionViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val printerUtils: PrinterUtils,
    private val sharePreference: ModuleSharePreference
) : ViewModel() {

    var productSelected: ProductsOrderModel = ProductsOrderModel()

    private var _liveDataRegisterOrder = MutableLiveData<Resource<Boolean>>()
    val liveDataRegisterOrder: LiveData<Resource<Boolean>> get() = _liveDataRegisterOrder

    private var _liveDataPrint = MutableLiveData<Resource<String>>()
    val liveDataPrint: LiveData<Resource<String>> get() = _liveDataPrint

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveOrder(order: OrderModel) {
        _liveDataRegisterOrder.value = Resource.loading(null)
        viewModelScope.launch {
            updateTableList(order.table.toInt())
            val date = Tools.getCurrentDate()
            _liveDataRegisterOrder.value = productsRepository.saveOrderRegister(date, order)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun printOrder(order: OrderModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentOrderNumber = sharePreference.getOrderNumber()
            val newOrderNumber = currentOrderNumber + 1
            sharePreference.saveCurrentOrderNumber(newOrderNumber)

            _liveDataPrint.postValue(
                printerUtils.printOrder(
                    order,
                    newOrderNumber,
                    App.printerPort,
                    App.printerAddress
                )
            )
        }
    }

    fun updateTableList(tablePosition: Int) {
        App.tablesAvailable.onEach {
            if (it.position == tablePosition.toString()) {
                it.available = false
            }
        }
    }

}