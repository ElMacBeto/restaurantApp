package com.humbjorch.restaurantapp.core.utils.printer

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import androidx.annotation.RequiresApi
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.tcp.TcpConnection
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.Tools.getCurrentDate
import com.humbjorch.restaurantapp.core.utils.Tools.getCurrentTime
import com.humbjorch.restaurantapp.data.datasource.remote.Resource
import com.humbjorch.restaurantapp.data.model.OrderModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class PrinterUtils @Inject constructor(val context: Context) {


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun printTicket(order: OrderModel): Resource<Unit> = suspendCoroutine { continuation ->
        try {
            val sock = Socket("192.168.100.123", 9100)
            val oStream = PrintWriter(sock.getOutputStream())

            oStream.println("***** Ticket de Compra *****")
            oStream.println(getCurrentDate())
            oStream.println("****************************")
            var total = 0
            for (product in order.productList) {
                val price = product.price.toInt() * product.amount.toInt()
                total += price
                val text = "x ${product.amount}  ${product.product} $${product.price}.00"
                oStream.println(text)
            }
            oStream.println("total: $total")
            oStream.println("\n¡Gracias por su compra!")
            oStream.println("\n\n\n\n\n")

            oStream.write(1)
            oStream.close()
            sock.close()

            continuation.resume(Resource.success(Unit))
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            continuation.resume(Resource.error(e.message.toString()))
        } catch (e: IOException) {
            e.printStackTrace()
            continuation.resume(Resource.error(e.message.toString()))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun printNewTicket(
        order: OrderModel,
        printerPort: Int,
        printerAddress: String
    ): Resource<String> = withContext(Dispatchers.IO) {
        try {
            val printer =
                EscPosPrinter(TcpConnection(printerAddress, printerPort, 1500), 203, 80f, 48)
            var total = 0
            val ticket = StringBuilder()

            ticket.append(
                "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
                    printer,
                    context.resources.getDrawableForDensity(
                        R.drawable.logo_bg_white,
                        DisplayMetrics.DENSITY_XXHIGH
                    )
                ) + "</img>\n\n"
            )
            ticket.append("[L]<b>fecha: ${getCurrentDate(true)}</b>\n")
            ticket.append("Calle Dr. Lucas Vallarta #84 Colonia Dr. Lucas Vallarta Tepic, Mexico\n")
            ticket.append("[C]************************************************\n")
            if (order.address.isNotEmpty()){
                ticket.append("[L]<b>Dirección:${order.address} </b>\n")
                ticket.append("[C]************************************************\n")
            }

            for (product in order.productList) {
                var extraPrice = 0
                val extrasText =
                    if (product.extras.isEmpty())
                        ""
                    else {
                        val extraPrices = product.extras.map { it.price.toInt() }
                        extraPrice = extraPrices.sum()
                        val extrasList = product.extras.map { it.name }
                        "extras: ${extrasList.joinToString()}"
                    }
                val price = (product.price.toInt() * product.amount.toInt()) + extraPrice
                total += price
                val text =
                    "[L]<b>x${product.amount} ${product.product} </b> [R]$$price.00\n"
                ticket.append(text)
                ticket.append(extrasText+"\n")
            }
            ticket.append("[C]************************************************\n")
            ticket.append("[R]<u><font size='big'>Total: $total</font></u>\n\n\n\n\n\n.")
            printer.printFormattedTextAndCut("$ticket")
            printer.disconnectPrinter()
            Resource.success(null)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.error(e.message.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun printOrder(
        order: OrderModel,
        orderNumber: Int,
        printerPort: Int,
        printerAddress: String
    ): Resource<String> = withContext(Dispatchers.IO) {
        try {
            val printer =
                EscPosPrinter(TcpConnection(printerAddress, printerPort, 1500), 203, 80f, 42)
            val ticket = StringBuilder()
            val tableText = if (order.table == "-1") "Para llevar" else "Meza: ${order.table}"
            ticket.append("[R]<u><font size='big-2'>$tableText</font></u>\n\n")
            ticket.append("[C]<u><font size='big'>ORDER #$orderNumber</font></u>\n\n")
            ticket.append("[R]hora: ${getCurrentTime()}\n")
            ticket.append("[C]================================================\n\n")

            for (product in order.productList) {
                val ingredientsText = if (product.ingredients.isEmpty()) "" else "-> ${product.ingredients.joinToString()}"
                val extrasText =
                    if (product.extras.isEmpty())
                        ""
                    else {
                        val extrasList = product.extras.map { it.name }
                        ", extras: ${extrasList.joinToString()}"
                    }
                val productText =
                    "[L]<font size='big'><b>x${product.amount}  ${product.product}</b> $ingredientsText" +
                            "\n ${product.otherName}: ${product.other}" +
                            "\n Extras: $extrasText</font>\n"
                ticket.append(productText)
            }

            ticket.append("\n\n\n\n" + ".")

            printer.printFormattedTextAndCut("$ticket")
            printer.disconnectPrinter()
            Resource.success(null)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.error(e.message.toString())
        }
    }

}

