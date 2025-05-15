package org.example.subscriptions.services

import org.example.subscriptions.data.Plan
import org.example.subscriptions.data.Subscription
import org.example.subscriptions.data.User

interface SubscriptionService {
    fun subscribe(user: User, plan: Plan): Subscription
    fun cancelSubscription(user: User): Subscription
    fun renewSubscription(user: User): Subscription
    fun upgradeSubscription(user: User, newPlan: Plan): Subscription
}
