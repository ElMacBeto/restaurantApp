package com.humbjorch.restaurantapp.core.di

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.humbjorch.restaurantapp.core.utils.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseClientModule @Inject constructor() {
    val productsCollection = Firebase.firestore.collection(Constants.PRODUCTS_COLLECTION)
    val tableCollection = Firebase.firestore.collection(Constants.TABLES_COLLECTION)
    val orderRegisterCollection = Firebase.firestore.collection(Constants.ORDERS_REGISTER_COLLECTION)
}