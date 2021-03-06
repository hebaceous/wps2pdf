package me.hebaceous.wps2pdf

import com.jacob.activeX.ActiveXComponent
import com.jacob.com.Dispatch

import java.io.File
import java.nio.file.Files
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit

object WPS {

  private val executorService = Executors.newCachedThreadPool()

  private fun doWord2pdf(word: File): File {
    val pdf = Files.createTempFile(null, ".pdf").toFile()
    val app = ActiveXComponent("Word.Application")
    try {
      app.setProperty("Visible", false)
      val docs = app.getProperty("Documents").toDispatch()
      val doc = Dispatch.call(docs, "Open", word.absolutePath, false, true).toDispatch()
      try {
        Dispatch.call(doc, "ExportAsFixedFormat", pdf.absolutePath, 17)
      } finally {
        Dispatch.call(doc, "Close", false)
      }
    } finally {
      app.invoke("Quit", 0)
    }
    return pdf
  }

  fun word2pdf(word: File): File {
    val future = FutureTask<File> { doWord2pdf(word) }
    executorService.submit(future)
    try {
      return future.get(30, TimeUnit.SECONDS)
    } catch (e: Exception) {
      future.cancel(true)
      throw RuntimeException(e)
    }
  }

}
