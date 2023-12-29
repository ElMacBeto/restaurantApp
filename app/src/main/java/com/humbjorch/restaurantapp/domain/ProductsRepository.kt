package com.humbjorch.restaurantapp.domain

import com.humbjorch.restaurantapp.core.utils.Status
import com.humbjorch.restaurantapp.data.model.ProductModel
import com.humbjorch.restaurantapp.data.datasource.remote.Resource
import com.humbjorch.restaurantapp.data.datasource.remote.webDb.ProductsWebDS
import javax.inject.Inject

class ProductsRepository @Inject constructor(
    private val productsWebDS: ProductsWebDS
) {

    suspend fun getAllProducts(): Resource<ProductModel> {
        val beverageResponse = productsWebDS.getBeverages()
        val hamburgerResponse = productsWebDS.getHamburgers()
        val tableAvailableResponse = productsWebDS.getTablesAvailable()

        if (beverageResponse.status == Status.ERROR) {
            return Resource.error(beverageResponse.message)
        }
        if (hamburgerResponse.status == Status.ERROR) {
            return Resource.error(hamburgerResponse.message)
        }
        if (tableAvailableResponse.status == Status.ERROR) {
            return Resource.error(tableAvailableResponse.message)
        }
        return Resource.success(
            ProductModel(
                beverages = beverageResponse.data ?: emptyList(),
                hamburgers = hamburgerResponse.data?.list ?: emptyList(),
                tablesAvailable = tableAvailableResponse.data?.tables ?: emptyList()
            )
        )
    }

}