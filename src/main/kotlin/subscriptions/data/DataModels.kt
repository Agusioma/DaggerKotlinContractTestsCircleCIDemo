package org.example.subscriptions.data

data class User(
    val id: String,
    val name: String,
    val email: String
)

data class Subscription(
    val userId: String,
    val plan: String,
    val active: Boolean
)

data class Payment(
    val userId: String,
    val amount: Double,
    val successful: Boolean
)
