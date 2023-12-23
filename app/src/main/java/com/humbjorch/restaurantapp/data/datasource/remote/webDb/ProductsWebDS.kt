package com.humbjorch.restaurantapp.data.datasource.remote.webDb

import com.humbjorch.restaurantapp.data.datasource.remote.api.FirebaseApiService
import javax.inject.Inject

class ProductsWebDS @Inject constructor(private val service: FirebaseApiService) {

    suspend fun getAllUsersTeams() = service.sendRegisterProducts()
}