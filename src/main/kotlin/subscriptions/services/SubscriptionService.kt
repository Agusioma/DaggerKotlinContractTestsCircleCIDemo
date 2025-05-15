package org.example.subscriptions.services

import org.example.subscriptions.data.Subscription
import org.example.subscriptions.data.User

interface SubscriptionService {
    fun subscribe(user: User, plan: String): Subscription
    fun cancelSubscription(user: User): Subscription
}