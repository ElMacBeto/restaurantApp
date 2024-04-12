package com.humbjorch.restaurantapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExtraModel(
    val name: String = "",
    val price: String = ""
): Parcelable
