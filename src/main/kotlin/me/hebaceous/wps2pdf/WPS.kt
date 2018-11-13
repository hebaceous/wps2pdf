package me.hebaceous.wps2pdf

import com.jacob.activeX.ActiveXComponent
import com.jacob.com.Dispatch

import java.io.File
import java.io.IOException
import java.nio.file.Files

object WPS {

    @Throws(IOException::class)
    fun word2pdf(word: File): File {
        val pdf = Files.createTempFile(null, ".pdf").toFile()
        val app = ActiveXComponent("Word.Application")
        app.setProperty("Visible", false)
        val docs = app.getProperty("Documents").toDispatch()
        val doc = Dispatch.call(docs, "Open", word.absolutePath, false, true).toDispatch()
        Dispatch.call(doc, "ExportAsFixedFormat", pdf.absolutePath, 17)
        Dispatch.call(doc, "Close", false)
        app.invoke("Quit", 0)
        return pdf
    }

}
