package com.hammad.payment_support_platform
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountRepository: AccountRepository
) {

    fun createAccount(
        customerName: String,
        phoneNumber: String
    ): Account {

        val account = Account(
            customerName = customerName,
            phoneNumber = phoneNumber
        )

        return accountRepository.save(account)
    }

    fun getAccount(id: Long): Account {
    return accountRepository.findById(id)
        .orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Account $id not found"
            )
        }
}
}