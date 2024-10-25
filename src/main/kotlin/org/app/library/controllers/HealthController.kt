package org.app.library.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.InetAddress

@RestController
@RequestMapping("api/library/health")
@CrossOrigin("http://localhost:3000")
class HealthController {

    @GetMapping("")
    fun health(): ResponseEntity<Map<String, String>> {
        val hostname = InetAddress.getLocalHost().hostName
        val ip = InetAddress.getLocalHost().hostAddress

        val response = mapOf(
            "status" to "healthy",
            "hostname" to hostname,
            "ip" to ip
        )

        return ResponseEntity.ok(response)
    }
}