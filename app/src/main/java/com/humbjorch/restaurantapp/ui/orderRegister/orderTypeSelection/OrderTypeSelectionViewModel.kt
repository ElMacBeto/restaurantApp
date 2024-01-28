package com.humbjorch.restaurantapp.ui.orderRegister.orderTypeSelection

import androidx.lifecycle.ViewModel
import com.humbjorch.restaurantapp.App

class OrderTypeSelectionViewModel : ViewModel() {

    fun updateTableList(tablePosition: Int) {
        App.tablesAvailable.onEach {
            if (it.position == tablePosition.toString()) {
                it.available = false
            }
        }
    }
}