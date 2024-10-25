package org.app.library.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class KeycloakTokenResponse(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("expires_in") val expires_in: String,
    @JsonProperty("refresh_expires_in") val refresh_expires_in: String,
    @JsonProperty("refresh_token") val refresh_token: String,
    @JsonProperty("token_type") val token_type: String,
    @JsonProperty("session_state") val session_state: String,
    @JsonProperty("scope") val scope: String,
    @JsonProperty("not-before-policy") val not_before_policy: String,
    // Include other fields as needed
)
