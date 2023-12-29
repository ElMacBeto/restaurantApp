package com.humbjorch.restaurantapp.core.utils

object Constants {
    const val PRODUCTS_COLLECTION = "Products"
    const val HAMBURGERS_COLLECTION = "Hamburgers"
    const val BEVERAGES_COLLECTION = "Beverages"
    const val TABLES_AVAILABLE_COLLECTION = "TablesAvailable"

    const val PATH_FIREBASE_STORAGE = "products"


}

enum class ProductType{
    HAMBURGER,
    DRINK,
    POTATOES,
    WINGS,
    BONELESS
}