package com.hammad.payment_support_platform

import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) {

    fun createTransaction(
        accountId: Long,
        amount: BigDecimal,
        type: String
    ): Transaction {

        val account = accountRepository.findById(accountId)
            .orElseThrow {
                RuntimeException("Account $accountId not found")
            }

        val transaction = Transaction(
            accountId = accountId,
            amount = amount,
            type = type
        )

        if (type == "CREDIT") {
            account.balance = account.balance.add(amount)
        } else if (type == "DEBIT") {
            account.balance = account.balance.subtract(amount)
        }

        accountRepository.save(account)

        return transactionRepository.save(transaction)
    }
}