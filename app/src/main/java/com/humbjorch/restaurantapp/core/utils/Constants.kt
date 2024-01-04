package com.humbjorch.restaurantapp.core.utils

object Constants {
    const val PRODUCTS_COLLECTION = "Products"
    const val TABLES_COLLECTION = "Tables"
    const val ORDERS_REGISTER_COLLECTION = "OrderRegister"

    const val TABLES_AVAILABLE_DOCUMENT = "TablesAvailable"
    const val HAMBURGERS_DOCUMENT = "Hamburgers"
    const val DRINKS_DOCUMENT = "Drinks"
    const val BONELESS_DOCUMENT = "Boneless"
    const val WINGS_DOCUMENT = "Wings"
    const val POTATOES_DOCUMENT = "Potatoes"

}

enum class ProductActionListener{
    ADD_PRODUCT,
    REMOVE_PRODUCT
}

