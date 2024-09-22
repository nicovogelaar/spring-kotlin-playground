package com.nicovogelaar.playground

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class ServerApplicationConfig {
    @Bean
    fun reactiveWebServerFactory(environment: Environment): ReactiveWebServerFactory {
        val factory = NettyReactiveWebServerFactory()
        val port = environment.getProperty("server.port")?.toIntOrNull() ?: 8080
        factory.port = port
        return factory
    }

    @Bean
    fun webFluxRouter(): RouterFunction<ServerResponse> {
        return RouterFunctions.route(GET("/hello")) {
            ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).bodyValue("Hello, World!")
        }
    }
}
