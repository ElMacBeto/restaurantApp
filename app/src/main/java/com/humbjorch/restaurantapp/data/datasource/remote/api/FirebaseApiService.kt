package com.humbjorch.restaurantapp.data.datasource.remote.api

import com.google.gson.Gson
import com.humbjorch.restaurantapp.core.di.FirebaseClientModule
import com.humbjorch.restaurantapp.core.utils.Constants.ORDER_NUMBER_DOCUMENT
import com.humbjorch.restaurantapp.core.utils.Constants.TABLES_AVAILABLE_DOCUMENT
import com.humbjorch.restaurantapp.data.datasource.remote.makeCall
import com.humbjorch.restaurantapp.data.datasource.remote.response.OrderNumberResponse
import com.humbjorch.restaurantapp.data.datasource.remote.response.ProductResponse
import com.humbjorch.restaurantapp.data.datasource.remote.response.TablesAvailableResponse
import com.humbjorch.restaurantapp.data.model.OrderListModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseApiService @Inject constructor(private val client: FirebaseClientModule) {

    suspend fun getAllProducts() = makeCall {
        client.productsCollection.get().await().documents.mapNotNull {
            val json = Gson().toJson(it.data)
            val product = Gson().fromJson(json, ProductResponse::class.java)
            product.id = it.id
            product
        }
    }

    suspend fun getAllTablesAvailable() = makeCall {
        client.tableCollection.document(TABLES_AVAILABLE_DOCUMENT).get().await().let {
            val tableList = it.toObject(TablesAvailableResponse::class.java)
            tableList
        }
    }

    suspend fun sendRegisterProducts(id: String, orderList: OrderListModel) =
        makeCall {
            client.orderRegisterCollection.document(id).set(
                orderList
            ).let { it ->
                var isSuccess = false
                it.addOnCompleteListener {
                    isSuccess = it.isSuccessful
                }.await()
                isSuccess
            }
        }

    suspend fun getOrdersRegisterByDate(date: String) = makeCall {
        client.orderRegisterCollection.document(date).get().await().let {
            val json = Gson().toJson(it.data)
            Gson().fromJson(json, OrderListModel::class.java)
        }
    }

    suspend fun getAllOrdersRegister() = makeCall {
        client.orderRegisterCollection.get().await().documents.mapNotNull {
            val json = Gson().toJson(it.data)
            val orderList = Gson().fromJson(json, OrderListModel::class.java)
            orderList.id = it.id
            orderList
        }
    }

    suspend fun updateTable(tables: TablesAvailableResponse) = makeCall {
        client.tableCollection.document(TABLES_AVAILABLE_DOCUMENT).set(tables).let { it ->
            var isSuccess = false
            it.addOnCompleteListener {
                isSuccess = it.isSuccessful
            }.await()
            isSuccess
        }
    }

    suspend fun getOrderNumber() = makeCall {
        client.tableCollection.document(ORDER_NUMBER_DOCUMENT).get().await().let {
            val json = Gson().toJson(it.data)
            val orderNumber = Gson().fromJson(json, OrderNumberResponse::class.java)
            orderNumber
        }
    }

    suspend fun updateOrderNumber(orderNumber: OrderNumberResponse) = makeCall {
        client.tableCollection.document(ORDER_NUMBER_DOCUMENT).set(orderNumber).let { it ->
            var isSuccess = false
            it.addOnCompleteListener {
                isSuccess = it.isSuccessful
            }.await()
            isSuccess
        }
    }

}
