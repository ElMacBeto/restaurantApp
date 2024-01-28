package com.humbjorch.restaurantapp.core.utils.printer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.view.View
import java.io.FileOutputStream

class MyPrintDocumentAdapter(private val content: View) : PrintDocumentAdapter() {

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
    ) {
        // Respond to layout requests by providing the layout information
        val info = PrintDocumentInfo.Builder("document.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .build()

        callback.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback
    ) {
        // Write the content to the output file descriptor
        val output = FileOutputStream(destination.fileDescriptor)

        // Convert the content view to a bitmap
        val bitmap = Bitmap.createBitmap(content.width, content.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        content.draw(canvas)

        // Write the bitmap to the output stream as a PDF
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvasPdf = page.canvas
        canvasPdf.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)
        pdfDocument.writeTo(output)

        // Close the output stream
        output.close()

        // Notify the print framework that the write operation is complete
        callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
    }
}