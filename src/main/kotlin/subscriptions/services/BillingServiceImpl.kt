package org.example.subscriptions.services

import javax.inject.Inject
import org.example.subscriptions.data.User
import org.example.subscriptions.data.Payment

class BillingServiceImpl @Inject constructor() : BillingService {

    override fun processPayment(user: User, amount: Double): Payment {
        // Simulate a random success/failure for payment processing
        val successful = Math.random() > 0.3
        return Payment(user.id, amount, successful)
    }

    override fun retryPayment(payment: Payment): Payment {
        // Simulate retrying payment, increasing retry count
        val successful = Math.random() > 0.5 // Retry has a better chance of success
        return payment.copy(successful = successful, retryCount = payment.retryCount + 1)
    }
}
