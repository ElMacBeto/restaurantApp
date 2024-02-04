package com.humbjorch.restaurantapp.data.mappers


import com.humbjorch.restaurantapp.data.datasource.remote.response.ProductResponse
import com.humbjorch.restaurantapp.data.model.ProductListModel

class ProductsMapper : Mapper<ProductResponse, ProductListModel> {

    override suspend fun map(input: ProductResponse): ProductListModel {
        return ProductListModel(
            imageUrl = input.imageUrl,
            list = input.list,
            name = input.name,
            extras = input.extras
        )
    }
}