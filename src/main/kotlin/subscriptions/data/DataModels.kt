package org.example.subscriptions.data

data class User(
    val id: String,
    val name: String,
    val email: String
)

data class Plan(
    val name: String,
    val price: Double,
    val billingCycle: BillingCycle
)

enum class BillingCycle {
    MONTHLY, YEARLY
}

data class Subscription(
    val userId: String,
    val plan: Plan,
    val active: Boolean,
    val startDate: Long,
    val endDate: Long,
    val nextBillingDate: Long
)

data class Payment(
    val userId: String,
    val amount: Double,
    val successful: Boolean,
    val retryCount: Int = 0
)
