package org.example.subscriptions.di

import org.example.subscriptions.services.SubscriptionService
import org.example.subscriptions.services.BillingService
import dagger.Component

@Component(modules = [AppModule::class])
interface AppComponent {
    fun subscriptionService(): SubscriptionService
    fun billingService(): BillingService
}
