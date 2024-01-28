package com.humbjorch.restaurantapp.ui.home

import android.R
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.core.di.ModuleSharePreference
import com.humbjorch.restaurantapp.core.utils.OrderStatus
import com.humbjorch.restaurantapp.core.utils.Tools
import com.humbjorch.restaurantapp.core.utils.printer.PrinterUtils
import com.humbjorch.restaurantapp.data.datasource.remote.Resource
import com.humbjorch.restaurantapp.data.model.OrderListModel
import com.humbjorch.restaurantapp.data.model.OrderModel
import com.humbjorch.restaurantapp.data.model.ProductListModel
import com.humbjorch.restaurantapp.domain.DataStorePreferencesRepository
import com.humbjorch.restaurantapp.domain.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val dataStorePreferencesRepository: DataStorePreferencesRepository,
    private val printerUtils: PrinterUtils,
    private val sharePreference: ModuleSharePreference
) : ViewModel() {

    private var _getAllOrdersLiveData = MutableLiveData<Resource<OrderListModel?>>()
    val getAllOrdersLiveData: LiveData<Resource<OrderListModel?>> get() = _getAllOrdersLiveData

    private var _updateOrderLiveData = MutableLiveData<Resource<Boolean>>()
    val updateOrderLiveData: LiveData<Resource<Boolean>> get() = _updateOrderLiveData

    private var _setProductsLiveData = MutableLiveData<Resource<List<ProductListModel>>>()
    val setProductsLiveData: LiveData<Resource<List<ProductListModel>>> get() = _setProductsLiveData

    private var _printLiveData = MutableLiveData<Resource<String>>()
    val printLiveData: LiveData<Resource<String>> get() = _printLiveData

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDayOrders() {
        viewModelScope.launch {
            _getAllOrdersLiveData.value = Resource.loading()
            val currentDate = Tools.getCurrentDate()
            _getAllOrdersLiveData.value = productsRepository.getOrders(currentDate)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun changePaidOrder(order: OrderModel) {
        _updateOrderLiveData.value = Resource.loading()
        viewModelScope.launch {
            val id = Tools.getCurrentDate()
            updateTable(order.table)
            order.status = OrderStatus.PAID.value
            productsRepository.saveOrderRegister(id, order)
            _updateOrderLiveData.value = productsRepository.saveOrderRegister(id, order)
        }
    }

    fun setAllProducts() {
        viewModelScope.launch {
            _setProductsLiveData.value = productsRepository.getAllProducts()
        }
    }

    fun getTableOrders() =
        App.ordersList.orders.filter { it.table.toInt() > 0 && it.status == OrderStatus.WITHOUT_PAYING.value }

    fun getDeliveryOrders() =
        App.ordersList.orders.filter { it.table.toInt() < 0 && it.status == OrderStatus.WITHOUT_PAYING.value }

    private fun updateTable(position: String) {
        if (position.toInt() < 1) return
        App.tablesAvailable.onEach {
            if (position == it.position)
                it.available = true
        }
    }

    fun setTotal(order: OrderModel): Int {
        var total = 0
        order.productList.forEach {
            val price = it.price.toInt() * it.amount.toInt()
            total += price
        }
        return total
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun print(order: OrderModel) {
        _printLiveData.value = Resource.loading()
        viewModelScope.launch(Dispatchers.IO) {
            val currentOrderNumber = sharePreference.getOrderNumber()

            _printLiveData.postValue(
                printerUtils.printNewTicket(
                    order,
                    currentOrderNumber,
                    App.printerPort,
                    App.printerAddress
                )
            )
        }
    }

    fun setPrinterSettings() {
        App.printerPort = sharePreference.getPrinterPort()
        App.printerAddress = sharePreference.getPrinterAddress() ?: ""
    }

}