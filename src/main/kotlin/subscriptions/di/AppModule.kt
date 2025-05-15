package org.example.subscriptions.di

import org.example.subscriptions.services.BillingService
import org.example.subscriptions.services.BillingServiceImpl
import org.example.subscriptions.services.SubscriptionService
import org.example.subscriptions.services.SubscriptionServiceImpl
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    fun provideBillingService(): BillingService = BillingServiceImpl()

    @Provides
    fun provideSubscriptionService(billingService: BillingService): SubscriptionService =
        SubscriptionServiceImpl(billingService)
}
