package com.humbjorch.restaurantapp.data.datasource.remote.response

import com.humbjorch.restaurantapp.data.model.HamburgerModel

data class HamburgerResponse(
    @field:JvmField
    val list: ArrayList<HamburgerModel> = arrayListOf()
)