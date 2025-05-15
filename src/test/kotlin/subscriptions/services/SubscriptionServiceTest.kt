package subscriptions.services

import org.example.subscriptions.data.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.subscriptions.services.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class SubscriptionServiceTest {

    private lateinit var subscriptionService: SubscriptionService
    private val billingService = mockk<BillingService>()

    @BeforeEach
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

        val subscription = subscriptionService.subscribe(user, plan)

        assertTrue(subscription.active)
        assertEquals(plan.name, subscription.plan.name)
        assertEquals(1, retryPayment.retryCount)

        verify(exactly = 1) { billingService.retryPayment(initialPayment) }
    }

    @Test
    fun `test subscription upgrade`() {
        val user = User("2", "Bob", "bob@example.com")
        val basicPlan = Plan("Basic", 5.0, BillingCycle.MONTHLY)
        val premiumPlan = Plan("Premium", 10.0, BillingCycle.MONTHLY)

        // Assume payment always succeeds
        every { billingService.processPayment(user, premiumPlan.price) } returns Payment(user.id, premiumPlan.price, true)

        // First subscribe to a basic plan
        every { billingService.processPayment(user, basicPlan.price) } returns Payment(user.id, basicPlan.price, true)
        subscriptionService.subscribe(user, basicPlan)

        // Upgrade subscription
        val upgraded = subscriptionService.upgradeSubscription(user, premiumPlan)

        assertEquals(premiumPlan.name, upgraded.plan.name)
        assertEquals(user.id, upgraded.userId)
        assertTrue(upgraded.active)
    }
}


