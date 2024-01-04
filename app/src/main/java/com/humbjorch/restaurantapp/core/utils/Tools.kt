package com.humbjorch.restaurantapp.core.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object Tools {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    fun generateID() = UUID.randomUUID().toString()
}