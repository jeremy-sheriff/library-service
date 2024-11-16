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
        // Extract global roles from realm_access.roles
        val globalRoles = (jwt.claims["realm_access"] as? Map<*, *>)?.get("roles") as? List<*>
            ?: emptyList<Any>()

        // Extract resource-specific roles from resource_access.students-service.roles
        val resourceAccess = jwt.claims["resource_access"] as? Map<*, *> ?: emptyMap<Any, Any>()
        val serviceRoles = (resourceAccess["students-service"] as? Map<*, *>)?.get("roles") as? List<*>
            ?: emptyList<Any>()

        // Combine both sets of roles without prefix
        val combinedRoles = (globalRoles + serviceRoles).mapNotNull { roleName ->
            SimpleGrantedAuthority(roleName.toString())
        }

        // Create a JwtAuthenticationToken with the combined roles
        return JwtAuthenticationToken(jwt, combinedRoles)
    }
}

