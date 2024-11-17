package com.nicovogelaar.playground

import com.nicovogelaar.playground.security.JwtTokenAuthenticationFilter
import com.nicovogelaar.playground.security.JwtTokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
@EnableAspectJAutoProxy(proxyTargetClass = true)
class SecurityConfig {
    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        jwtTokenProvider: JwtTokenProvider,
        reactiveAuthenticationManager: ReactiveAuthenticationManager,
    ): SecurityWebFilterChain {
        return http.csrf { it.disable() }
            .httpBasic { it.disable() }
            .authenticationManager(reactiveAuthenticationManager)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers(HttpMethod.POST, "/auth/login").permitAll()
                    .anyExchange().authenticated()
            }
            .addFilterAt(
                JwtTokenAuthenticationFilter(jwtTokenProvider),
                SecurityWebFiltersOrder.AUTHENTICATION,
            )
            .build()
    }

    @Bean
    fun reactiveAuthenticationManager(
        userDetailsService: ReactiveUserDetailsService,
        passwordEncoder: PasswordEncoder,
    ): ReactiveAuthenticationManager {
        val authenticationManager = UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService)
        authenticationManager.setPasswordEncoder(passwordEncoder)
        return authenticationManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun userDetailsService(passwordEncoder: PasswordEncoder): ReactiveUserDetailsService {
        return ReactiveUserDetailsService { username ->
            if (username == "dummyUser") {
                Mono.just(
                    User.withUsername("dummyUser")
                        .password(passwordEncoder.encode("dummyPassword"))
                        .authorities("ROLE_USER")
                        .accountExpired(false)
                        .credentialsExpired(false)
                        .disabled(false)
                        .accountLocked(false)
                        .build(),
                )
            } else {
                Mono.empty()
            }
        }
    }
}
