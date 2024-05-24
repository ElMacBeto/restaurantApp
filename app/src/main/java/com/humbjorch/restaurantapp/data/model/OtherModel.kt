package com.humbjorch.restaurantapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OtherModel(
    val name: String = "",
    val type: String = ""
): Parcelable
