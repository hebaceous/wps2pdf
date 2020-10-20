package me.hebaceous.wps2pdf

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import javax.servlet.http.HttpServletResponse
import java.util.concurrent.TimeUnit

@Controller
@RequestMapping
class Controller {

    private val logger = LoggerFactory.getLogger(me.hebaceous.wps2pdf.Controller::class.java)

    @PostMapping("/word2pdf")
    fun word2pdf(@RequestPart word: MultipartFile, response: HttpServletResponse) {
        val filename = word.originalFilename!!
        logger.info("word2pdf:{}", filename)
        val lastDotIndex = filename.lastIndexOf('.')
        if (lastDotIndex == -1) throw RuntimeException("bad file name")
        val suffix = filename.substring(lastDotIndex)
        val tempWordFile = Files.createTempFile(null, suffix).toFile()
        word.transferTo(tempWordFile)
        val pdfFile = WPS.word2pdf(tempWordFile).get(1, TimeUnit.MINUTES)
        logger.info("word2pdf:[{}]->[{}]", tempWordFile.absolutePath, pdfFile.absolutePath)
        response.outputStream.write(pdfFile.readBytes())
    }

}
