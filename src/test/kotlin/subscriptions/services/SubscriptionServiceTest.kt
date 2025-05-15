package subscriptions.services

import org.example.subscriptions.data.Payment
import org.example.subscriptions.data.User
import org.example.subscriptions.services.BillingService
import org.example.subscriptions.services.BillingServiceImpl
import org.example.subscriptions.services.SubscriptionService
import org.example.subscriptions.services.SubscriptionServiceImpl
import io.mockk.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SubscriptionServiceTest {

    private lateinit var subscriptionService: SubscriptionService
    private val billingService = mockk<BillingService>()

    @Before
    fun setUp() {
        subscriptionService = SubscriptionServiceImpl(billingService)
    }

    @Test
    fun `test subscribe with retries`() {
        val user = User("1", "Alice", "alice@example.com")
        val plan = Plan("Premium", 10.0, BillingCycle.MONTHLY)

        // Simulate a failed first payment, and then successful retry
        val initialPayment = Payment(user.id, plan.price, false)
        val retryPayment = initialPayment.copy(successful = true, retryCount = 1)

        every { billingService.processPayment(user, plan.price) } returns initialPayment
        every { billingService.retryPayment(initialPayment) } returns retryPayment

        // Attempt to subscribe
        val subscription = subscriptionService.subscribe(user, plan)

        // Verify subscription creation after retrying payment
        assertEquals(true, subscription.active)
        assertEquals(plan.name, subscription.plan.name)
        assertEquals(1, retryPayment.retryCount)

        // Verify that retryPayment was called exactly once
        verify(exactly = 1) { billingService.retryPayment(initialPayment) }
    }

    @Test
    fun `test subscription upgrade`() {
        val user = User("2", "Bob", "bob@example.com")
        val basicPlan = Plan("Basic", 5.0, BillingCycle.MONTHLY)
        val premiumPlan = Plan("Premium", 10.0, BillingCycle.MONTHLY)

        // Mock existing subscription
        val existingSubscription = Subscription(user.id, basicPlan, true, System.currentTimeMillis(), 0, 0)
        every { billingService.processPayment(user, premiumPlan.price) } returns Payment(user.id, premiumPlan.price, true)

        // Upgrade subscription to premium
        val upgradedSubscription = subscriptionService.upgradeSubscription(user, premiumPlan)

        assertEquals(premiumPlan.name, upgradedSubscription.plan.name)
        assertEquals(existingSubscription.userId, upgradedSubscription.userId)
    }
}

