package com.humbjorch.restaurantapp.core.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.humbjorch.restaurantapp.data.model.OrderModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.Calendar
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateWithTime(): String {
        val dateFormat = SimpleDateFormat("MM-dd-yyyy hh:mm a", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDateForHistory(): String {
        val dateFormat = SimpleDateFormat("d-MMMM-yyyy", Locale("es", "ES"))
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    fun getDateFormatted(
        dayOfMonth: Int,
        monthOfYear: Int,
        year: Int,
        isFormat: Boolean = false
    ): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, monthOfYear, dayOfMonth)
        val dateFormat = if (isFormat) {
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        } else
            SimpleDateFormat("d-MMMM-yyyy", Locale("es", "ES"))

        return dateFormat.format(calendar.time)
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

    fun getTotalForList(orders: List<OrderModel>): Int {
        var total = 0
        for (order in orders) {
            if (order.status == OrderStatus.PAID.value)
                order.productList.forEach {
                    val extraPrice = it.extras.sumOf { extra -> extra.price.toInt() }
                    val price = it.price.toInt() * it.amount.toInt()
                    total += price + extraPrice
                }
        }
        return total
    }
}