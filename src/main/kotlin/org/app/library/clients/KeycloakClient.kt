package org.app.library.clients

import feign.codec.Encoder
import feign.form.spring.SpringFormEncoder
import org.app.library.dto.KeycloakTokenResponse
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.cloud.openfeign.support.SpringEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Configuration
class FormFeignEncoderConfig {

    @Bean
    fun encoder(converters: ObjectFactory<HttpMessageConverters>): Encoder {
        return SpringFormEncoder(SpringEncoder(converters))
    }
}
@FeignClient(name = "keycloakClient",
    url = "\${spring.security.oauth2.resourceserver.jwt.issuer-uri}",
    configuration = [FormFeignEncoderConfig::class]
)
interface KeycloakClient {
    @PostMapping(path = ["/token"], consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun getToken(@RequestParam data: Map<String, String>): KeycloakTokenResponse



}
