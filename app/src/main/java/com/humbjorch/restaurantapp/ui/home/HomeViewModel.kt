package com.humbjorch.restaurantapp.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.core.utils.Tools.getCurrentDate
import com.humbjorch.restaurantapp.data.datasource.remote.Resource
import com.humbjorch.restaurantapp.data.model.OrderListModel
import com.humbjorch.restaurantapp.domain.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productsRepository: ProductsRepository
):ViewModel() {

    private var _getAllOrdersLiveData = MutableLiveData<Resource<OrderListModel?>>()
    val getAllOrdersLiveData: LiveData<Resource<OrderListModel?>> get() = _getAllOrdersLiveData

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllOrders(){
        viewModelScope.launch {
            val currentDate = getCurrentDate()
            _getAllOrdersLiveData.value = productsRepository.getOrders(currentDate)
        }
    }

    fun getTableOrders() = App.ordersList.orders.filter { it.table.toInt() > 0 }

    fun getDeliveryOrders() = App.ordersList.orders.filter { it.table.toInt() < 0 }


}