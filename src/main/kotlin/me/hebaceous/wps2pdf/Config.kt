package me.hebaceous.wps2pdf

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RequestPredicates.POST
import org.springframework.web.reactive.function.server.RequestPredicates.accept
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route

@Configuration
class Config {
    @Bean
    fun routerFunction(handler: Handler): RouterFunction<*> {
        return route(POST("/word2pdf").and(accept(MediaType.MULTIPART_FORM_DATA)), HandlerFunction(handler::word2pdf))
    }
}
