package com.humbjorch.restaurantapp.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.core.di.ModuleSharePreference
import com.humbjorch.restaurantapp.data.datasource.remote.Resource
import com.humbjorch.restaurantapp.data.model.ProductListModel
import com.humbjorch.restaurantapp.domain.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val sharePreference: ModuleSharePreference
) : ViewModel() {

    private var _setProductsLiveData = MutableLiveData<Resource<List<ProductListModel>>>()
    val setProductsLiveData: LiveData< Resource<List<ProductListModel>>> get() = _setProductsLiveData

    fun setAllProducts() {
        viewModelScope.launch {
           _setProductsLiveData.value = productsRepository.getAllProducts()
        }
    }

    fun setPrinterSettings() {
        App.printerPort = sharePreference.getPrinterPort()
        App.printerAddress = sharePreference.getPrinterAddress() ?: ""
    }
}