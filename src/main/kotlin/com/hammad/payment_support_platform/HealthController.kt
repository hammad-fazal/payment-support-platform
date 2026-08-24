package com.hammad.payment_support_platform

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/api/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "service" to "payment-support-platform"
        )
    }
}