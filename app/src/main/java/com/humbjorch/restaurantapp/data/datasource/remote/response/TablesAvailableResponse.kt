package com.humbjorch.restaurantapp.data.datasource.remote.response

import com.humbjorch.restaurantapp.data.model.TableAvailableModel

data class TablesAvailableResponse(
    @field:JvmField
    val tables: ArrayList<TableAvailableModel> = arrayListOf()
)
