package org.example.subscriptions.services

import org.example.subscriptions.data.Payment
import org.example.subscriptions.data.User

interface BillingService {
    fun processPayment(user: User, amount: Double): Payment
}