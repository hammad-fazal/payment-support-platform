package com.hammad.payment_support_platform

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "transactions")
class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var accountId: Long,

    var amount: BigDecimal,

    var type: String,

    var status: String = "COMPLETED",

    var createdAt: LocalDateTime = LocalDateTime.now()
)