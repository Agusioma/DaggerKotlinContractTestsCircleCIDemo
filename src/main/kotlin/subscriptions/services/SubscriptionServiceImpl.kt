package org.example.subscriptions.services

import org.example.subscriptions.data.Plan
import org.example.subscriptions.data.Subscription
import org.example.subscriptions.data.User
import org.example.subscriptions.data.BillingCycle
import javax.inject.Inject
import kotlin.math.ceil

class SubscriptionServiceImpl @Inject constructor(
    private val billingService: BillingService
) : SubscriptionService {

    private val subscriptions = mutableListOf<Subscription>()

    override fun subscribe(user: User, plan: Plan): Subscription {
        // 1. First, we attempt to process payment
        var payment = billingService.processPayment(user, plan.price)

        // 2. If payment fails, retry up to 3 times
        var retries = payment.retryCount
        while (!payment.successful && retries < 3) {
            retries++
            println("Retrying payment... Attempt #$retries")
            payment = billingService.retryPayment(payment)
        }

        // 3. If payment is successful, create subscription
        if (payment.successful) {
            val currentTime = System.currentTimeMillis()
            val subscription = Subscription(
                userId = user.id,
                plan = plan,
                active = true,
                startDate = currentTime,
                endDate = currentTime + plan.billingCycle.billingPeriodInMillis(),
                nextBillingDate = currentTime + plan.billingCycle.billingPeriodInMillis()
            )
            subscriptions.add(subscription)
            return subscription
        } else {
            throw IllegalStateException("Payment failed after multiple retries.")
        }
    }

    override fun cancelSubscription(user: User): Subscription {
        val subscription = subscriptions.find { it.userId == user.id }
            ?: throw IllegalStateException("Subscription not found.")
        return subscription.copy(active = false)
    }

    override fun renewSubscription(user: User): Subscription {
        val subscription = subscriptions.find { it.userId == user.id }
            ?: throw IllegalStateException("Subscription not found.")

        // Renew subscription by extending the end date
        val currentTime = System.currentTimeMillis()
        val renewedSubscription = subscription.copy(
            active = true,
            startDate = currentTime,
            endDate = currentTime + subscription.plan.billingCycle.billingPeriodInMillis(),
            nextBillingDate = currentTime + subscription.plan.billingCycle.billingPeriodInMillis()
        )
        subscriptions.remove(subscription)
        subscriptions.add(renewedSubscription)
        return renewedSubscription
    }

    override fun upgradeSubscription(user: User, newPlan: Plan): Subscription {
        val subscription = subscriptions.find { it.userId == user.id }
            ?: throw IllegalStateException("Subscription not found.")

        // Upgrade plan, extending the end date and updating the plan
        val currentTime = System.currentTimeMillis()
        val upgradedSubscription = subscription.copy(
            plan = newPlan,
            active = true,
            startDate = currentTime,
            endDate = currentTime + newPlan.billingCycle.billingPeriodInMillis(),
            nextBillingDate = currentTime + newPlan.billingCycle.billingPeriodInMillis()
        )
        subscriptions.remove(subscription)
        subscriptions.add(upgradedSubscription)
        return upgradedSubscription
    }

    // Helper method to simulate billing cycle periods
    private fun BillingCycle.billingPeriodInMillis(): Long {
        return when (this) {
            BillingCycle.MONTHLY -> 30L * 24L * 60L * 60L * 1000L
            BillingCycle.YEARLY -> 365L * 24L * 60L * 60L * 1000L
        }
    }
}

