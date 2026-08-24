package com.hammad.payment_support_platform

import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/transactions")
class TransactionController(
    private val transactionService: TransactionService
) {

    @PostMapping
    fun createTransaction(
        @RequestBody request: CreateTransactionRequest
    ): Transaction {

        return transactionService.createTransaction(
            request.accountId,
            request.amount,
            request.type
        )
    }
}

data class CreateTransactionRequest(
    val accountId: Long,
    val amount: BigDecimal,
    val type: String
)