package com.humbjorch.restaurantapp.core.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.humbjorch.restaurantapp.data.model.OrderModel
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.UUID

object Tools {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(withFormat: Boolean = false): String {
        val dateFormat =
            if (!withFormat) {
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            } else
                SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy", Locale("es", "ES"))

        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentTime(): String {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return currentTime.format(formatter)
    }

    fun generateID() = UUID.randomUUID().toString()

    fun getTotal(order: OrderModel): Int {
        var total = 0
        order.productList.forEach {
            val extraPrice = it.extras.sumOf { extra -> extra.price.toInt() }
            val price = it.price.toInt() * it.amount.toInt()
            total += price + extraPrice
        }
        return total
    }
}