package com.humbjorch.restaurantapp.ui.history

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.core.utils.Status
import com.humbjorch.restaurantapp.core.utils.printer.PrinterUtils
import com.humbjorch.restaurantapp.data.datasource.remote.Resource
import com.humbjorch.restaurantapp.data.model.OrderModel
import com.humbjorch.restaurantapp.domain.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val printerUtils: PrinterUtils,
): ViewModel() {

    private var _printLiveData = MutableLiveData<Resource<String>>()
    val printLiveData: LiveData<Resource<String>> get() = _printLiveData

    private var _getAllOrdersLiveData = MutableLiveData<Resource<List<OrderModel>>>()
    val getAllOrdersLiveData: LiveData<Resource<List<OrderModel>>> get() = _getAllOrdersLiveData


    @RequiresApi(Build.VERSION_CODES.O)
    fun printTicket(order: OrderModel){
        viewModelScope.launch {
            val response = printerUtils.printNewTicket(
                order,
                App.printerPort,
                App.printerAddress
            )
            if (response.status == Status.ERROR)
                _printLiveData.value = response
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllDayOrders(startDate: LocalDate, endDate: LocalDate) {
        _getAllOrdersLiveData.value = Resource.loading()
        viewModelScope.launch {
            _getAllOrdersLiveData.value = productsRepository.getAllOrdersRegister(startDate, endDate)
        }
    }

}