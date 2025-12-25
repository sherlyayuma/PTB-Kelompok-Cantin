package com.example.cantin.model

data class Order(
    val userId: Int = 0, 
    val id: String = java.util.UUID.randomUUID().toString(),
    val items: List<CartItem>,
    val timestamp: Long,
    val isTakeAway: Boolean = false,
    val durationInMillis: Long = 1 * 60 * 1000L, 
    val isCompleted: Boolean = false
)
