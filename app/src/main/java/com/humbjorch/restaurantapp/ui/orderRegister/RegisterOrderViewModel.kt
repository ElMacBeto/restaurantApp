package com.humbjorch.restaurantapp.ui.orderRegister

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.core.di.ModuleSharePreference
import com.humbjorch.restaurantapp.core.utils.Status
import com.humbjorch.restaurantapp.core.utils.Tools
import com.humbjorch.restaurantapp.core.utils.printer.PrinterUtils
import com.humbjorch.restaurantapp.data.datasource.remote.Resource
import com.humbjorch.restaurantapp.data.datasource.remote.response.OrderNumberResponse
import com.humbjorch.restaurantapp.data.model.OrderModel
import com.humbjorch.restaurantapp.data.model.ProductsOrderModel
import com.humbjorch.restaurantapp.domain.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterOrderViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val printerUtils: PrinterUtils,
    private val sharePreference: ModuleSharePreference
) : ViewModel() {

    private var _liveDataRegisterOrder = MutableLiveData<Resource<Boolean>>()
    val liveDataRegisterOrder: LiveData<Resource<Boolean>> get() = _liveDataRegisterOrder

    private var _liveDataSetTables = MutableLiveData<Resource<Unit>>()
    val liveDataSetTables: LiveData<Resource<Unit>> get() = _liveDataSetTables

    var tableSelected: Int = 1
    var orderAddress: String = ""
    var order: OrderModel? = null

    var productSelection = MutableLiveData(ProductsOrderModel())
    var productList: List<ProductsOrderModel> = emptyList()

    private var _liveDataPrint = MutableLiveData<Resource<String>>()
    val liveDataPrint: LiveData<Resource<String>> get() = _liveDataPrint

    var isEditOrder = false
    var orderNumber = 1


    @RequiresApi(Build.VERSION_CODES.O)
    fun saveOrder() {
        _liveDataRegisterOrder.value = Resource.loading(null)

        val currentOrder = order ?: OrderModel()
        val orderModel = OrderModel(
            id = if (currentOrder.id != "") currentOrder.id else Tools.generateID(),
            table = tableSelected.toString(),
            productList = productList,
            time = Tools.getCurrentTime(),
            address = orderAddress
        )
        order = orderModel

        viewModelScope.launch {
            val response = productsRepository.getOrderNumber()
            if (response.status == Status.ERROR){
                _liveDataRegisterOrder.value =  Resource.error(response.message)
                return@launch
            }

            orderNumber = (response.data ?: 1) + 1

            val updateResponse = productsRepository.updateOrderNumber(OrderNumberResponse(orderNumber.toLong()))
            if (updateResponse.status == Status.ERROR){
                _liveDataRegisterOrder.value =  Resource.error(updateResponse.message)
                return@launch
            }

            orderModel.orderNumber = orderNumber
            updateTableList(orderModel.table.toInt())
            val date = Tools.getCurrentDate()
            _liveDataRegisterOrder.value = productsRepository.saveOrderRegister(date, orderModel)
        }
    }

    private fun updateTableList(tablePosition: Int) {
        App.tablesAvailable.onEach {
            if (it.position == tablePosition.toString()) {
                it.available = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun printOrder() {
        viewModelScope.launch(Dispatchers.IO) {
            //val currentOrderNumber = sharePreference.getOrderNumber()
            //val newOrderNumber = currentOrderNumber + 1
            //sharePreference.saveCurrentOrderNumber(newOrderNumber)
            val newOrder = order!!

            _liveDataPrint.postValue(
                printerUtils.printOrder(
                    newOrder,
                    //newOrderNumber,
                    App.printerPort,
                    App.printerAddress
                )
            )
        }
    }

    private fun setOrderNumber(){
        viewModelScope.launch {
            val response = productsRepository.getOrderNumber()
            if (response.status == Status.ERROR)
                _liveDataRegisterOrder.value =  Resource.error(response.message)
            else
                orderNumber = response.data ?: 1
        }
    }


    fun setTables(){
        _liveDataSetTables.value = Resource.loading(null)
        viewModelScope.launch {
            _liveDataSetTables.value = productsRepository.getTables()
        }
    }

}