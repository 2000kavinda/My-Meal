package org.myapp.mymeal.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel {
    private val _currentUserEmail = MutableStateFlow<String?>(null)
    val currentUserEmail: StateFlow<String?> = _currentUserEmail

    fun setCurrentUserEmail(email: String) {
        _currentUserEmail.value = email
    }

    private val _currentUserGender = MutableStateFlow<String?>(null)
    val currentUserGender: StateFlow<String?> = _currentUserGender

    fun setCurrentUserGender(gender: String) {
        _currentUserGender.value = gender
    }

    private val _currentUserActivityLevel = MutableStateFlow<String?>(null)
    val currentUserActivityLevel: StateFlow<String?> = _currentUserActivityLevel

    fun setCurrentUserActivityLevel(activityLevel: String) {
        _currentUserActivityLevel.value = activityLevel
    }

    private val _currentUserGoal = MutableStateFlow<String?>(null)
    val currentUserGoal: StateFlow<String?> = _currentUserGoal

    fun setCurrentUserGoal(goal: String) {
        _currentUserGoal.value = goal
    }
    private val _payAmount = MutableStateFlow<Double?>(0.0)
    val payAmount: StateFlow<Double?> = _payAmount

    fun setPayAmount(amount: Double) {
        _payAmount.value = amount
    }

    private val _coinAmount = MutableStateFlow<Double?>(0.0)
    val coinAmount: StateFlow<Double?> = _coinAmount

    fun setCoinAmount(coin: Double) {
        _coinAmount.value = coin
    }
}
