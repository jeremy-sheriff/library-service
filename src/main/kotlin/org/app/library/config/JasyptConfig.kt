package org.app.library.config

import org.jasypt.encryption.StringEncryptor
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

//@Configuration
class JasyptConfig {
    @Value("\${jasypt.encryptor.password}")
    private lateinit var password: String

    @Value("\${jasypt.encryptor.algorithm}")
    private lateinit var algorithm: String

    @Bean("jasyptStringEncryptor")
    fun stringEncryptor(): StringEncryptor {

        val encryptor = StandardPBEStringEncryptor()
        encryptor.setPassword(password) // Use a strong password/key
        encryptor.setAlgorithm(algorithm) // Choose the algorithm (can use a stronger one)
        return encryptor
    }
}