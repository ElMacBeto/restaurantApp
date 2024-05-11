package com.humbjorch.restaurantapp.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.core.di.ModuleSharePreference
import com.humbjorch.restaurantapp.core.utils.OrderStatus
import com.humbjorch.restaurantapp.core.utils.Status
import com.humbjorch.restaurantapp.core.utils.Tools
import com.humbjorch.restaurantapp.core.utils.printer.PrinterUtils
import com.humbjorch.restaurantapp.data.datasource.remote.Resource
import com.humbjorch.restaurantapp.data.model.OrderListModel
import com.humbjorch.restaurantapp.data.model.OrderModel
import com.humbjorch.restaurantapp.data.model.ProductListModel
import com.humbjorch.restaurantapp.domain.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewHomeViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val printerUtils: PrinterUtils,
    private val sharePreference: ModuleSharePreference
) : ViewModel() {


    private var _getAllOrdersLiveData = MutableLiveData<Resource<OrderListModel?>>()
    val getAllOrdersLiveData: LiveData<Resource<OrderListModel?>> get() = _getAllOrdersLiveData

    private var _printLiveData = MutableLiveData<Resource<String>>()
    val printLiveData: LiveData<Resource<String>> get() = _printLiveData

    private var _updateOrderLiveData = MutableLiveData<Resource<Boolean>>()
    val updateOrderLiveData: LiveData<Resource<Boolean>> get() = _updateOrderLiveData

    private var _setProductsLiveData = MutableLiveData<Resource<List<ProductListModel>>>()
    val setProductsLiveData: LiveData<Resource<List<ProductListModel>>> get() = _setProductsLiveData

    private var _cleanTablesLiveData = MutableLiveData<Resource<Boolean>>()
    val cleanTablesLiveData: LiveData<Resource<Boolean>> get() = _cleanTablesLiveData

    fun getTableOrders() =
        App.ordersList.orders.filter {
            it.table.toInt() >= 0 && it.status == OrderStatus.WITHOUT_PAYING.value
        }

    fun getDeliveryOrders() =
        App.ordersList.orders.filter {
            it.table.toInt() < 0 && it.status == OrderStatus.WITHOUT_PAYING.value
        }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDayOrders() {
        viewModelScope.launch {
            _getAllOrdersLiveData.value = Resource.loading()
            val currentDate = Tools.getCurrentDate()
            _getAllOrdersLiveData.value = productsRepository.getOrders(currentDate)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun printOrder(order: OrderModel) {
        _printLiveData.value = Resource.loading()
        viewModelScope.launch(Dispatchers.IO) {
            val currentOrderNumber = sharePreference.getOrderNumber()

            _printLiveData.postValue(
                printerUtils.printOrder(
                    order,
                    currentOrderNumber,
                    App.printerPort,
                    App.printerAddress
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun cancelOrder(order: OrderModel) {
        _updateOrderLiveData.value = Resource.loading()
        viewModelScope.launch {
            val id = Tools.getCurrentDate()
            updateTable(order.table)
            order.status = OrderStatus.CANCEL.value
            _updateOrderLiveData.value = productsRepository.saveOrderRegister(id, order)
        }
    }

    private fun updateTable(position: String) {
        if (position.toInt() < 1) return
        App.tablesAvailable.onEach {
            if (position == it.position)
                it.available = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun changePaidOrder(order: OrderModel) {
        _updateOrderLiveData.value = Resource.loading()
        viewModelScope.launch {
            val day = Tools.getCurrentDate()
            updateTable(order.table)
            order.status = OrderStatus.PAID.value
            _updateOrderLiveData.value = productsRepository.saveOrderRegister(day, order)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun printTicket(order:OrderModel){
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

    fun setAllProducts() {
        viewModelScope.launch {
            _setProductsLiveData.value = productsRepository.getAllProducts()
        }
    }

    fun cleanTableAvailable(){
        _cleanTablesLiveData.value = Resource.loading()
        viewModelScope.launch {
            App.tablesAvailable.onEach {
                it.available = true
            }
            _cleanTablesLiveData.value = productsRepository.updateTables()
        }
    }
}