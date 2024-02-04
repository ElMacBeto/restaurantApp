package com.humbjorch.restaurantapp

import android.app.Application
import com.humbjorch.restaurantapp.data.model.ExtraModel
import com.humbjorch.restaurantapp.data.model.OrderListModel
import com.humbjorch.restaurantapp.data.model.ProductListModel
import com.humbjorch.restaurantapp.data.model.TableAvailableModel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App:Application() {

    companion object{

        var tablesAvailable = emptyList<TableAvailableModel>()
        var productListModel = emptyList<ProductListModel>()
        var ordersList = OrderListModel()
        var printerPort = 0
        var printerAddress = ""
    }

}