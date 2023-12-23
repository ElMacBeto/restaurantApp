package com.humbjorch.restaurantapp.core.di

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.humbjorch.restaurantapp.core.utils.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseClientModule @Inject constructor() {
    val userCollection = Firebase.firestore.collection(Constants.PRODUCTS_COLLECTION)
}