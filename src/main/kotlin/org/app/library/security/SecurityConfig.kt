package org.app.library.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig{

    @Value("\${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    val jwkSetUri = ""

    private val jwtAuthConverter = JwtAuthConverter()

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http.csrf { it ->
            it.disable()
        }.authorizeHttpRequests { it ->

            it.requestMatchers(
                "/api/library/health",
                "/actuator/health",
                "/actuator/info",
                "/actuator/metrics",
                "/actuator/prometheus")
                .permitAll()

            // Explicitly allow all OPTIONS requests to pass through (for CORS preflight)
            it.requestMatchers("/**").permitAll()
            it.requestMatchers("/**").permitAll()  // Ensure OPTIONS requests are allowed
            it.anyRequest().authenticated()
        }

        http. oauth2ResourceServer{  it->
            it.jwt{ x ->
                x.decoder(jwtDecoder())
                x.jwtAuthenticationConverter(jwtAuthConverter)
            }
        }

        http.sessionManagement{
            it.sessionCreationPolicy(STATELESS )
        }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("http://localhost:4200")
        config.addAllowedOrigin("https://muhohodev.com")
        config.addAllowedOrigin("https://api.muhohodev.com")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        config.maxAge = 3600L

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }
}