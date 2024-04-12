package com.humbjorch.restaurantapp.data.datasource.remote.response

import com.humbjorch.restaurantapp.data.model.ExtraModel

data class ExtrasResponse(
    @field:JvmField
    val list: ArrayList<ExtraModel> = arrayListOf()
)