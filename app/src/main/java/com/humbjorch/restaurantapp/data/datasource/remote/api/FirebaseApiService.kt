package com.humbjorch.restaurantapp.data.datasource.remote.api

import com.google.firebase.firestore.toObject
import com.humbjorch.restaurantapp.core.di.FirebaseClientModule
import com.humbjorch.restaurantapp.core.utils.Constants.BEVERAGES_COLLECTION
import com.humbjorch.restaurantapp.core.utils.Constants.HAMBURGERS_COLLECTION
import com.humbjorch.restaurantapp.core.utils.Constants.TABLES_AVAILABLE_COLLECTION
import com.humbjorch.restaurantapp.data.model.BeverageModel
import com.humbjorch.restaurantapp.data.model.HamburgerModel
import com.humbjorch.restaurantapp.data.datasource.remote.makeCall
import com.humbjorch.restaurantapp.data.datasource.remote.response.HamburgerResponse
import com.humbjorch.restaurantapp.data.datasource.remote.response.TablesAvailableResponse
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseApiService @Inject constructor( private val client: FirebaseClientModule ) {

    suspend fun getAllBeverages() = makeCall {
        client.productsCollection.document(BEVERAGES_COLLECTION).get().await().let{
            val beveragesList: List<BeverageModel> = it.get("all") as List<BeverageModel>
            beveragesList
        }
    }
    suspend fun getAllHamburgers() = makeCall {
        client.productsCollection.document(HAMBURGERS_COLLECTION).get().await().let{
            val hamburgerList = it.toObject(HamburgerResponse::class.java)
            hamburgerList
        }
    }

    suspend fun getAllTablesAvailable() = makeCall {
        client.productsCollection.document(TABLES_AVAILABLE_COLLECTION).get().await().let {
            val tableList = it.toObject(TablesAvailableResponse::class.java)
            tableList
        }
    }


}
