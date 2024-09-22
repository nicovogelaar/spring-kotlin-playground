package com.nicovogelaar.playground

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.RequestPredicates.GET

@Configuration
class ServerApplicationConfig {
    @Bean
    fun reactiveWebServerFactory(): ReactiveWebServerFactory {
        return NettyReactiveWebServerFactory()
    }

    @Bean
    fun webFluxRouter(): RouterFunction<ServerResponse> {
        return RouterFunctions.route(GET("/hello")) {
            ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).bodyValue("Hello, World!")
        }
    }
}
