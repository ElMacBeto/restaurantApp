package com.humbjorch.restaurantapp.data.datasource.remote.api

import com.humbjorch.restaurantapp.core.di.FirebaseClientModule
import com.humbjorch.restaurantapp.data.datasource.model.ProductModel
import com.humbjorch.restaurantapp.data.datasource.model.ProductTypeModel
import com.humbjorch.restaurantapp.data.datasource.remote.makeCall
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseApiService @Inject constructor(private val client: FirebaseClientModule) {

//    suspend fun getAllUsers() = makeCall {
//        client.userCollection.get().await().documents.mapNotNull {
//            it.toObject(UserHomeResponse::class.java)
//        }
//    }
suspend fun sendRegisterProducts() =
    makeCall {
        client.userCollection.document("Hamburger").set(
            ProductModel(
                name = "Hamburguesa",
                types = listOf(
                    ProductTypeModel(
                        type = "Papi Burger",
                        description = "150gr de carne de res con jitomate, cebolla y queso gouda",
                        price = 85,
                        image = "agregar",
                        ingredients = listOf(
                            "con todo",
                            "sin jitomate",
                            "sin cebolla",
                            "sin lechuga"
                        )
                    ),ProductTypeModel(
                        type = "Papi Champi",
                        description = "150gr de carne de res con champiñones sasonados a la papi, jitomate, cebolla y queso gouda",
                        price = 95,
                        image = "agregar",
                        ingredients = listOf(
                            "con todo",
                            "sin jitomate",
                            "sin cebolla",
                            "sin lechuga"
                        )
                    ),
                    ProductTypeModel(
                        type = "Papi Chiken",
                        description = "150gr de carne de pollo con adereso de cool morada, jitomate, cebolla",
                        price = 95,
                        image = "agregar",
                        ingredients = listOf(
                            "con todo",
                            "sin adereso"
                        )
                    ),
                    ProductTypeModel(
                        type = "Papi Bacon",
                        description = "150gr de carne de res con tocino, jitomate, cebolla y queso gouda",
                        price = 95,
                        image = "agregar",
                        ingredients = listOf(
                            "con todo",
                            "sin jitomate",
                            "sin cebolla",
                            "sin lechuga"
                        )
                    ),
                    ProductTypeModel(
                        type = "Papi del Mar",
                        description = "100gr de camaron con pimientos sasonados a la papi gratinado con queso gouda, cebolla",
                        price = 95,
                        image = "agregar",
                        ingredients = listOf(
                            "con todo",
                            "sin queso",
                            "sin pimientos",
                        )
                    )
                )
            )
        ).let { it ->
            var isSuccess = false
            it.addOnCompleteListener {
                isSuccess = it.isSuccessful
            }.await()
            isSuccess
        }
    }
//    @RequiresApi(Build.VERSION_CODES.O)
//   suspend fun getAllProducts() = makeCall {
//        var products = ProductsResponse
//        client.userCollection.get().await().documents.let {
//                it.forEach { j ->
//                    products = ProductsResponse(
//                        j.get("email") as ArrayList<String>,
//                        j.get("currentDay") as String
//                    )
//                }
//            products.emails
//        }
//    }

}
