package com.hammad.payment_support_platform

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.math.BigDecimal

@Entity
class Account(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var customerName: String,

    var phoneNumber: String,

    var balance: BigDecimal = BigDecimal.ZERO,

    var status: String = "ACTIVE"
)