package org.example.subscriptions.services

import org.example.subscriptions.data.Subscription
import org.example.subscriptions.data.User
import javax.inject.Inject

class SubscriptionServiceImpl @Inject constructor(
    private val billingService: BillingService
) : SubscriptionService {

    private val subscriptions = mutableListOf<Subscription>()

    override fun subscribe(user: User, plan: String): Subscription {
        val payment = billingService.processPayment(user, 10.0) // Amount is hardcoded for simplicity
        if (payment.successful) {
            val subscription = Subscription(user.id, plan, true)
            subscriptions.add(subscription)
            return subscription
        }
        throw IllegalStateException("Payment failed.")
    }

    override fun cancelSubscription(user: User): Subscription {
        val subscription = subscriptions.find { it.userId == user.id }
            ?: throw IllegalStateException("Subscription not found.")
        return subscription.copy(active = false)
    }
}
