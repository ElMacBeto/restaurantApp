package com.humbjorch.restaurantapp

import android.app.Application
import com.humbjorch.restaurantapp.data.model.BeverageModel
import com.humbjorch.restaurantapp.data.model.HamburgerModel
import com.humbjorch.restaurantapp.data.model.TableAvailableModel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App:Application() {

    companion object{
        var hamburgers = emptyList<HamburgerModel>()
        var beverages = emptyList<BeverageModel>()
        var tablesAvailable = emptyList<TableAvailableModel>()
    }

}