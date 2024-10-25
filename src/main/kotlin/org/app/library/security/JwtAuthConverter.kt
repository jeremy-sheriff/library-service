package org.app.library.security


import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Component
class JwtAuthConverter : Converter<Jwt, AbstractAuthenticationToken> {

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val resourceAccess = jwt.claims["resource_access"] as? Map<*, *> ?: emptyMap<Any, Any>()
        val roles = (resourceAccess["students-service"] as? Map<*, *>)?.get("roles") as? List<*> ?: emptyList<Any>()

        val grantedAuthorities = roles
            .mapNotNull { roleName -> SimpleGrantedAuthority(roleName.toString()) }

        return JwtAuthenticationToken(jwt, grantedAuthorities)
    }
}
