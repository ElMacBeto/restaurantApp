package com.humbjorch.restaurantapp.data.datasource.remote.webDb

import com.humbjorch.restaurantapp.data.datasource.remote.api.FirebaseApiService
import com.humbjorch.restaurantapp.data.datasource.remote.response.TablesAvailableResponse
import com.humbjorch.restaurantapp.data.model.TableAvailableModel
import javax.inject.Inject

class ProductsWebDS @Inject constructor(private val service: FirebaseApiService) {

    suspend fun getProduct(product: String) = service.getProduct(product)

    suspend fun getTablesAvailable() = service.getAllTablesAvailable()

    suspend fun updateTable(tables: TablesAvailableResponse) = service.updateTable(tables)

}