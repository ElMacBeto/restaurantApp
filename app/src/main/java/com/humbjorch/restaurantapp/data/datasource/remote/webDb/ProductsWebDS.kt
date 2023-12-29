package com.humbjorch.restaurantapp.data.datasource.remote.webDb

import com.humbjorch.restaurantapp.data.datasource.remote.api.FirebaseApiService
import javax.inject.Inject

class ProductsWebDS @Inject constructor(private val service: FirebaseApiService) {

    suspend fun getBeverages() = service.getAllBeverages()
    suspend fun getHamburgers() = service.getAllHamburgers()
    suspend fun getTablesAvailable() = service.getAllTablesAvailable()
}