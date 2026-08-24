package com.hammad.payment_support_platform

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/accounts")
class AccountController(
    private val accountService: AccountService
) {

    @PostMapping
    fun createAccount(
        @RequestBody request: CreateAccountRequest
    ): Account {

        return accountService.createAccount(
            request.customerName,
            request.phoneNumber
        )
    }

    @GetMapping("/{id}")
    fun getAccount(
        @PathVariable id: Long
    ): Account {

        return accountService.getAccount(id)
    }
}

data class CreateAccountRequest(
    val customerName: String,
    val phoneNumber: String
)