package com.humbjorch.restaurantapp.data.model

data class HamburgerModel(
    val name: String = "",
    val description: String = "",
    val price: String = "0",
    val image: String = "",
    val ingredients: List<String> = emptyList(),
    var isClicked:Boolean = false
)
