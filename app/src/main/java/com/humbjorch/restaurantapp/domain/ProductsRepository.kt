package com.humbjorch.restaurantapp.domain

import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.core.utils.Status
import com.humbjorch.restaurantapp.data.datasource.remote.Resource
import com.humbjorch.restaurantapp.data.datasource.remote.response.OrderNumberResponse
import com.humbjorch.restaurantapp.data.datasource.remote.response.TablesAvailableResponse
import com.humbjorch.restaurantapp.data.datasource.remote.webDb.OrdersWebDS
import com.humbjorch.restaurantapp.data.datasource.remote.webDb.ProductsWebDS
import com.humbjorch.restaurantapp.data.mappers.ProductsMapper
import com.humbjorch.restaurantapp.data.model.OrderModel
import com.humbjorch.restaurantapp.data.model.ProductListModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

    suspend fun saveOrderRegister(day: String, order: OrderModel): Resource<Boolean> {
        var updateData = false
        App.ordersList.orders.onEach {
            if (it.id == order.id) {
                updateData = true
                it.status = order.status
                it.table = order.table
                it.productList = order.productList
                it.time = order.time
                it.status = order.status
                it.total = order.total
                it.address = order.address
            }
        }
        if (!updateData) {
            val newList = App.ordersList.orders.plus(order)
            App.ordersList.orders = newList
        }

        if (App.tablesAvailable.isNotEmpty()){
            val tableResponse = productsWebDS.updateTable(
                TablesAvailableResponse(ArrayList(App.tablesAvailable))
            )
            if (tableResponse.status == Status.ERROR) {
                return Resource.error(tableResponse.message)
            }
        }

        return ordersWebDS.setOrderRegister(day, App.ordersList)
    }

    suspend fun getOrders(date: String) = ordersWebDS.getOrdersRegister(date)

    suspend fun getAllOrdersRegister(
        startDate: LocalDate,
        endDate: LocalDate
    ): Resource<List<OrderModel>> {
        val response = ordersWebDS.getAllOrdersRegister()

        return if (response.status == Status.SUCCESS) {
            val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")

            val filterOrders = response.data?.filter {
                val orderDate = LocalDate.parse(it.id, dateFormat)
                orderDate.isAfter(startDate.minusDays(1)) && orderDate.isBefore(endDate.plusDays(1))
            } ?: emptyList()

            val allOrders = filterOrders.flatMap {
                it.orders
            }

            Resource.success(allOrders)
        } else {
            Resource.error(response.message)
        }
    }

    suspend fun getTables():Resource<Unit>{
        val tableAvailableResponse = productsWebDS.getTablesAvailable()

        return if (tableAvailableResponse.status == Status.ERROR)
            Resource.error(tableAvailableResponse.message)
        else{
            App.tablesAvailable = tableAvailableResponse.data!!.list
            Resource.success(null)
        }
    }

    suspend fun updateTables(): Resource<Boolean> {
        return if (App.tablesAvailable.isEmpty()){
            Resource.error()
        }else{
            productsWebDS.updateTable(
                TablesAvailableResponse(ArrayList(App.tablesAvailable))
            )
        }
    }

    suspend fun getOrderNumber(): Resource<Int>{
        val orderNumberResponse = productsWebDS.getOrderNumber()
        return if (orderNumberResponse.status == Status.ERROR)
            Resource.error(orderNumberResponse.message)
        else
            Resource.success(orderNumberResponse.data?.value?.toInt())
    }

    suspend fun updateOrderNumber(orderNumber: OrderNumberResponse): Resource<Boolean>{
        val orderNumberResponse = productsWebDS.updateOrderNumber(orderNumber)
        return if (orderNumberResponse.status == Status.ERROR)
            Resource.error(orderNumberResponse.message)
        else
            orderNumberResponse
    }

}