package com.example.hackathon.model

data class BalanceDTO(
    val available: Long,
    val estimated: Long)

data class WalletDTO(val balance: BalanceDTO,
                     val address: String,
                     val userId: String)