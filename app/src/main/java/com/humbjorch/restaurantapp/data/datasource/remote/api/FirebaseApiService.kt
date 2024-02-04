package com.humbjorch.restaurantapp.data.datasource.remote.api

import com.google.gson.Gson
import com.humbjorch.restaurantapp.core.di.FirebaseClientModule
import com.humbjorch.restaurantapp.core.utils.Constants
import com.humbjorch.restaurantapp.core.utils.Constants.TABLES_AVAILABLE_DOCUMENT
import com.humbjorch.restaurantapp.data.datasource.remote.makeCall
import com.humbjorch.restaurantapp.data.datasource.remote.response.ExtrasResponse
import com.humbjorch.restaurantapp.data.datasource.remote.response.ProductResponse
import com.humbjorch.restaurantapp.data.datasource.remote.response.TablesAvailableResponse
import com.humbjorch.restaurantapp.data.model.OrderListModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseApiService @Inject constructor(private val client: FirebaseClientModule) {

    suspend fun getProduct(document: String) = makeCall {
        client.productsCollection.document(document).get().await().let {
            val response = it.toObject(ProductResponse::class.java)
            response
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

    suspend fun getOrdersRegister(id: String) = makeCall {
        client.orderRegisterCollection.document(id).get().await().let {
            val json = Gson().toJson(it.data)
            Gson().fromJson(json, OrderListModel::class.java)
        }
    }

    suspend fun updateTable(tables: TablesAvailableResponse) = makeCall {
        client.tableCollection.document(TABLES_AVAILABLE_DOCUMENT).set(
            tables
        ).let { it ->
            var isSuccess = false
            it.addOnCompleteListener {
                isSuccess = it.isSuccessful
            }.await()
            isSuccess
        }
    }
}
