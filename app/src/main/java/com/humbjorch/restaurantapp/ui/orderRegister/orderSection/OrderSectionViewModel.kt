package com.humbjorch.restaurantapp.ui.orderRegister.orderSection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humbjorch.restaurantapp.data.datasource.remote.Resource
import com.humbjorch.restaurantapp.data.model.OrderModel
import com.humbjorch.restaurantapp.domain.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderSectionViewModel @Inject constructor(
    private val productsRepository: ProductsRepository
):ViewModel() {

    private var _liveDataRegisterOrder = MutableLiveData<Resource<Boolean>>()
    val liveDataRegisterOrder: LiveData<Resource<Boolean>> get() = _liveDataRegisterOrder

    fun saveOrder(id: String, order: OrderModel){
        _liveDataRegisterOrder.value = Resource.loading(null)
        viewModelScope.launch {
            _liveDataRegisterOrder.value = productsRepository.saveOrderRegister(id, order)
        }
    }

}