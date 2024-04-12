package com.humbjorch.restaurantapp.domain

import com.humbjorch.restaurantapp.App
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
        val tableAvailableResponse = productsWebDS.getTablesAvailable()
        val allProductsResponse = productsWebDS.getAllProducts()

        val responses = listOf(
            allProductsResponse,
            tableAvailableResponse,
        )

        responses.forEach {
            if (it.status == Status.ERROR)
                return Resource.error(it.message)
        }

        val products = allProductsResponse.data!!.map { product ->
            ProductsMapper().map(product)
        }
        val sortedProducts = products.sortedBy { it.orderNum }

        App.tablesAvailable = tableAvailableResponse.data!!.list
        App.productListModel = sortedProducts
        return Resource.success(sortedProducts)
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