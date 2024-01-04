package com.humbjorch.restaurantapp.data.datasource.remote.webDb

import com.humbjorch.restaurantapp.data.datasource.remote.api.FirebaseApiService
import com.humbjorch.restaurantapp.data.model.OrderListModel
import javax.inject.Inject

class OrdersWebDS @Inject constructor(private val service: FirebaseApiService) {

    suspend fun setOrderRegister(id: String, order: OrderListModel) =
        service.sendRegisterProducts(id, order)

    suspend fun getOrdersRegister(date:String) = service.getOrdersRegister(date)

}