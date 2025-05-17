package subscriptions.services

import io.mockk.mockk
import org.example.subscriptions.services.BillingService
import org.example.subscriptions.services.SubscriptionService
import org.example.subscriptions.services.SubscriptionServiceImpl

class SubscriptionServiceImplContractTest : SubscriptionServiceContractTest() {
    override val billingService = mockk<BillingService>(relaxed = true)
    override val subscriptionService: SubscriptionService = SubscriptionServiceImpl(billingService)
}