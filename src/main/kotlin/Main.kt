package org.example

import org.example.subscriptions.data.BillingCycle
import org.example.subscriptions.data.Plan
import org.example.subscriptions.data.User
import org.example.subscriptions.di.DaggerAppComponent
import org.example.subscriptions.services.SubscriptionService

fun main() {
    val component = DaggerAppComponent.create()
    val subscriptionService: SubscriptionService = component.subscriptionService()

    val user = User(
        id = "user-001",
        name = "Jane Developer",
        email = "jane@demo.io"
    )

    val basicPlan = Plan(
        name = "Basic Plan",
        price = 19.99,
        billingCycle = BillingCycle.MONTHLY
    )

    val proPlan = Plan(
        name = "Pro Plan",
        price = 49.99,
        billingCycle = BillingCycle.MONTHLY
    )

    try {
        println("Subscribing user ${user.name} to ${basicPlan.name}")
        val subscription = subscriptionService.subscribe(user, basicPlan)
        println("Subscription successful: $subscription\n")

        println("Upgrading subscription to ${proPlan.name}...")
        val upgraded = subscriptionService.upgradeSubscription(user, proPlan)
        println("Subscription upgraded: $upgraded\n")

        println("Renewing subscription...")
        val renewed = subscriptionService.renewSubscription(user)
        println("Subscription renewed: $renewed\n")

        println("Cancelling subscription...")
        val cancelled = subscriptionService.cancelSubscription(user)
        println("Subscription cancelled: $cancelled")

    } catch (e: Exception) {
        println("Operation failed: ${e.message}")
    }
}

