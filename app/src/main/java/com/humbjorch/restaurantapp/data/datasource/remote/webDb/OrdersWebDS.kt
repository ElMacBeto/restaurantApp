package com.humbjorch.restaurantapp.data.datasource.remote.webDb

import com.humbjorch.restaurantapp.data.datasource.remote.api.FirebaseApiService
import com.humbjorch.restaurantapp.data.model.OrderListModel
import javax.inject.Inject

class OrdersWebDS @Inject constructor(private val service: FirebaseApiService) {

    suspend fun setOrderRegister(day: String, order: OrderListModel) =
        service.sendRegisterProducts(day, order)

    suspend fun getOrdersRegister(date:String) = service.getOrdersRegisterByDate(date)

    suspend fun getAllOrdersRegister() = service.getAllOrdersRegister()


}