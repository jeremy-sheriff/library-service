package org.app.library.clients

import feign.RequestInterceptor
import feign.RequestTemplate
import org.app.library.dto.StudentExistsResponse
import org.app.library.dto.UserDto
import org.app.library.security.KeyCloakTokenService
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Configuration
class FeignClientConfig(
    val keyCloakTokenService: KeyCloakTokenService
) {
    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor { template: RequestTemplate ->
            val accessToken = keyCloakTokenService.getToken()
            template.header("Authorization", "Bearer $accessToken")
        }
    }
}

@FeignClient(
    name = "user-client",
    url = "\${students.url}",
    path = "students",
    configuration = [FeignClientConfig::class]
)
interface UsersClient {
    @GetMapping("")
    fun getAllUsers(): MutableList<UserDto>

    @GetMapping("/{admNo}")
    fun getUserByAdmNo(@PathVariable admNo: String): UserDto

    @GetMapping("/id/{id}")
    fun getUserById(@PathVariable id: String): UserDto

    @GetMapping("/exists/{admNo}")
    fun studentExists(@PathVariable admNo: String): StudentExistsResponse

    @GetMapping("/ids/{ids}")
    fun getUsersWhereInIds(@PathVariable ids: String): MutableList<UserDto>
}
