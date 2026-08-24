package com.hammad.payment_support_platform

import org.springframework.data.jpa.repository.JpaRepository

interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun findByAccountId(accountId: Long): List<Transaction>
}