package com.humbjorch.restaurantapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OthersModel(
    val name: String = "",
    val type: List<String> = emptyList()
): Parcelable