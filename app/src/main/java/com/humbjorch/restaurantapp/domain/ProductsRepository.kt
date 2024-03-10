package com.humbjorch.restaurantapp.domain

import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.core.utils.Constants.BONELESS_DOCUMENT
import com.humbjorch.restaurantapp.core.utils.Constants.CHILIPOP_DOCUMENT
import com.humbjorch.restaurantapp.core.utils.Constants.DRINKS_DOCUMENT
import com.humbjorch.restaurantapp.core.utils.Constants.HAMBURGERS_DOCUMENT
import com.humbjorch.restaurantapp.core.utils.Constants.POTATOES_DOCUMENT
import com.humbjorch.restaurantapp.core.utils.Constants.WINGS_DOCUMENT
import com.humbjorch.restaurantapp.core.utils.Status
import com.humbjorch.restaurantapp.data.datasource.remote.Resource
import com.humbjorch.restaurantapp.data.datasource.remote.response.TablesAvailableResponse
import com.humbjorch.restaurantapp.data.datasource.remote.webDb.OrdersWebDS
import com.humbjorch.restaurantapp.data.datasource.remote.webDb.ProductsWebDS
import com.humbjorch.restaurantapp.data.mappers.ProductsMapper
import com.humbjorch.restaurantapp.data.model.OrderModel
import com.humbjorch.restaurantapp.data.model.ProductListModel
import javax.inject.Inject

class ProductsRepository @Inject constructor(
    private val productsWebDS: ProductsWebDS,
    private val ordersWebDS: OrdersWebDS
) {

    suspend fun getAllProducts(): Resource<List<ProductListModel>> {
        val drinksResponse = productsWebDS.getProduct(DRINKS_DOCUMENT)
        val hamburgerResponse = productsWebDS.getProduct(HAMBURGERS_DOCUMENT)
        val bonelessResponse = productsWebDS.getProduct(BONELESS_DOCUMENT)
        val wingsResponse = productsWebDS.getProduct(WINGS_DOCUMENT)
        val potatoesResponse = productsWebDS.getProduct(POTATOES_DOCUMENT)
        val chiliPopResponse = productsWebDS.getProduct(CHILIPOP_DOCUMENT)
        val tableAvailableResponse = productsWebDS.getTablesAvailable()

        val responses = listOf(
            drinksResponse,
            hamburgerResponse,
            bonelessResponse,
            wingsResponse,
            potatoesResponse,
            tableAvailableResponse,
            chiliPopResponse
        )

        responses.forEach {
            if (it.status == Status.ERROR)
                return Resource.error(it.message)
        }

        val allProducts = listOf(
            ProductsMapper().map(hamburgerResponse.data!!),
            ProductsMapper().map(drinksResponse.data!!),
            ProductsMapper().map(bonelessResponse.data!!),
            ProductsMapper().map(wingsResponse.data!!),
            ProductsMapper().map(potatoesResponse.data!!),
            ProductsMapper().map(chiliPopResponse.data!!),
        )

        App.tablesAvailable = tableAvailableResponse.data!!.list
        App.productListModel = allProducts
        return Resource.success(allProducts)
    }

    suspend fun saveOrderRegister(id: String, order: OrderModel): Resource<Boolean> {
        var updateData = false
        App.ordersList.orders.forEach {
            if (it.id == order.id) {
                updateData = true
                it.productList = order.productList
                it.time = order.time
                it.table = order.table
                it.address = order.address
            }
        }
        if (!updateData) {
            val newList = App.ordersList.orders.plus(order)
            App.ordersList.orders = newList
        }
        val tableResponse = productsWebDS.updateTable(
            TablesAvailableResponse(ArrayList(App.tablesAvailable))
        )

        if (tableResponse.status == Status.ERROR) {
            return Resource.error(tableResponse.message)
        }

        return ordersWebDS.setOrderRegister(id, App.ordersList)
    }

    suspend fun getOrders(date: String) = ordersWebDS.getOrdersRegister(date)

    suspend fun getAllOrdersRegister(startDate: String, endDate: String): Resource<List<OrderModel>> {
        val response = ordersWebDS.getAllOrdersRegister()

        return if (response.status == Status.SUCCESS) {
            val filterOrders = response.data?.filter {
                it.id in startDate..endDate
            }

            val allOrders = filterOrders?.flatMap {
                it.orders
            } ?: emptyList()

            Resource.success(allOrders)
        }else{
            Resource.error(response.message)
        }
    }


    suspend fun updateTables(): Resource<Boolean> {
        return productsWebDS.updateTable(
            TablesAvailableResponse(ArrayList(App.tablesAvailable))
        )
    }

}