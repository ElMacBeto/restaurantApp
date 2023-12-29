package com.humbjorch.restaurantapp.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.data.model.ProductModel
import com.humbjorch.restaurantapp.data.model.TableAvailableModel
import com.humbjorch.restaurantapp.data.datasource.remote.Resource
import com.humbjorch.restaurantapp.domain.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val productsRepository: ProductsRepository
) : ViewModel() {

    private var _setProductsLiveData = MutableLiveData<Resource<ProductModel>>()
    val setProductsLiveData: LiveData<Resource<ProductModel>> get() = _setProductsLiveData

    fun setAllProducts() {
        viewModelScope.launch {
           _setProductsLiveData.value = productsRepository.getAllProducts()
        }
    }

    fun setProducts(products: ProductModel) {
        App.hamburgers = products.hamburgers
        App.beverages = products.beverages
        App.tablesAvailable = products.tablesAvailable
    }
}