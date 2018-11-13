package me.hebaceous.wps2pdf

import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.badRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.nio.file.Files

@Component
class Handler {

    private val logger = LoggerFactory.getLogger(Handler::class.java)

    fun word2pdf(request: ServerRequest): Mono<ServerResponse> {
        return request.body(BodyExtractors.toMultipartData()).flatMap {
            val wordPart = it.getFirst("word") as FilePart?
                    ?: return@flatMap badRequest().body(fromObject("missing word"))
            logger.info("word2pdf:{}", wordPart.filename())
            val lastDotIndex = wordPart.filename().lastIndexOf('.')
            if (lastDotIndex == -1) return@flatMap badRequest().body(fromObject("bad file name"))
            val suffix = wordPart.filename().substring(lastDotIndex)
            val tempWordFile = Files.createTempFile(null, suffix).toFile()
            wordPart.transferTo(tempWordFile).subscribe()
            val pdfFile = WPS.word2pdf(tempWordFile)
            logger.info("word2pdf:[{}]->[{}]", tempWordFile.absolutePath, pdfFile.absolutePath)
            ok().contentType(MediaType.APPLICATION_PDF).body(pdfFile.readBytes().toMono(), ByteArray::class.java)
        }
    }
}