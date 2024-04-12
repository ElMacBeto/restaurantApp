package com.humbjorch.restaurantapp.core.utils

object Constants {
    const val PRODUCTS_COLLECTION = "Products"
    const val TABLES_COLLECTION = "Tables"
    const val ORDERS_REGISTER_COLLECTION = "OrderRegister"

    const val TABLES_AVAILABLE_DOCUMENT = "TablesAvailable"

    //preference
    const val SETTING_PREFERENCE = "SETTINGS_PREFERENCE"
    const val PRINTER_PORT_KEY = "printer_port_key"
    const val PRINTER_ADDRESS_KEY = "printer_address_key"
    const val ORDER_NUMBER_KEY = "order_number_key"

    const val PRODUCT_DEFAULT_AMOUNT = "1"
}

enum class ProductActionListener {
    ADD_PRODUCT,
    REMOVE_PRODUCT
}

enum class OrderStatus(val value: String) {
    PAID("PAID"),
    WITHOUT_PAYING("WITHOUT_PAYING"),
    CANCEL("CANCEL")
}

enum class ProductType(val value:String){
    BEVERAGE("BEVERAGE"),
    FOOD("FOOD")
}

