package subscriptions.services

import org.example.subscriptions.data.*
import org.example.subscriptions.services.BillingService
import org.example.subscriptions.services.SubscriptionService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import io.mockk.*
import org.junit.jupiter.api.BeforeEach

abstract class SubscriptionServiceContractTest {

    protected abstract val billingService: BillingService
    protected abstract val subscriptionService: SubscriptionService

    @BeforeEach
    open fun setUp() {
        clearMocks(billingService)
    }

    @Test
    fun `subscribe should retry payment and activate subscription`() {
        val user = User("1", "Alice", "alice@example.com")
        val plan = Plan("Premium", 10.0, BillingCycle.MONTHLY)

        val failedPayment = Payment(user.id, plan.price, false)
        val successfulRetry = failedPayment.copy(successful = true, retryCount = 1)

        every { billingService.processPayment(user, plan.price) } returns failedPayment
        every { billingService.retryPayment(failedPayment) } returns successfulRetry

        val subscription = subscriptionService.subscribe(user, plan)

        assertTrue(subscription.active)
        assertEquals(plan.name, subscription.plan.name)
        verify { billingService.retryPayment(failedPayment) }
    }

    @Test
    fun `upgradeSubscription should activate new plan`() {
        val user = User("2", "Bob", "bob@example.com")
        val oldPlan = Plan("Basic", 5.0, BillingCycle.MONTHLY)
        val newPlan = Plan("Premium", 15.0, BillingCycle.MONTHLY)

        every { billingService.processPayment(user, oldPlan.price) } returns Payment(user.id, oldPlan.price, true)
        subscriptionService.subscribe(user, oldPlan)

        every { billingService.processPayment(user, newPlan.price) } returns Payment(user.id, newPlan.price, true)

        val upgraded = subscriptionService.upgradeSubscription(user, newPlan)

        assertEquals(newPlan.name, upgraded.plan.name)
        assertTrue(upgraded.active)
    }

    @Test
    fun `cancelSubscription should deactivate existing subscription`() {
        val user = User("3", "Charlie", "charlie@example.com")
        val plan = Plan("Standard", 8.0, BillingCycle.MONTHLY)

        every { billingService.processPayment(user, plan.price) } returns Payment(user.id, plan.price, true)
        val activeSubscription = subscriptionService.subscribe(user, plan)

        val cancelled = subscriptionService.cancelSubscription(user)

        assertFalse(cancelled.active)
        assertEquals(activeSubscription.userId, cancelled.userId)
    }

    @Test
    fun `renewSubscription should update dates and keep subscription active`() {
        val user = User("4", "Dana", "dana@example.com")
        val plan = Plan("Monthly", 12.0, BillingCycle.MONTHLY)

        every { billingService.processPayment(user, plan.price) } returns Payment(user.id, plan.price, true)
        val originalSubscription = subscriptionService.subscribe(user, plan)

        Thread.sleep(1) // ensure timestamps will differ for realism

        val renewed = subscriptionService.renewSubscription(user)

        assertTrue(renewed.active)
        assertEquals(originalSubscription.userId, renewed.userId)
        assertTrue(renewed.startDate > originalSubscription.startDate)
        assertTrue(renewed.endDate > originalSubscription.endDate)
    }

}
