package com.humbjorch.restaurantapp.core.utils.printer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.print.PrintJobId
import android.print.PrintManager
import android.util.DisplayMetrics
import androidx.annotation.RequiresApi
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.tcp.TcpConnection
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.Tools.getCurrentDate
import com.humbjorch.restaurantapp.data.datasource.remote.Resource
import com.humbjorch.restaurantapp.data.model.OrderModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
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
        orderNumber: Int,
        printerPort: Int,
        printerAddress: String
    ): Resource<String> = withContext(Dispatchers.IO) {
            try {
                val printer =
                    EscPosPrinter(TcpConnection(printerAddress, printerPort, 1500), 203, 80f, 42)
                var total = 0
                val ticket = StringBuilder()
                for (product in order.productList) {
                    val price = product.price.toInt() * product.amount.toInt()
                    total += price
                    val text =
                        "[L]<b>x${product.amount}  ${product.product}</b> [R]$${product.price}.00\n"
                    ticket.append(text)
                }
                ticket.append("[C]--------------------------\n")
                ticket.append("[C]----ORDER $orderNumber----\n")
                ticket.append(
                    "[C]<u><font size='big'>Total: $total</font></u>\n\n\n\n\n\n" +
                            "."
                )
                printer.printFormattedTextAndCut(
                    "[L]${getCurrentDate(true)}\n" +
                            "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
                        printer,
                        context.resources.getDrawableForDensity(
                            R.drawable.logo_papiz2,
                            DisplayMetrics.DENSITY_MEDIUM
                        )
                    ) + "</img>\n\n" +
                            "[C]================================\n" +
                            ticket + "\n\n\n\n"
                )
                printer.disconnectPrinter()
                Resource.success(null)
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(e.message.toString())
            }
        }

}

