package org.myapp.mymeal

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel {
    private val _currentUserEmail = MutableStateFlow<String?>(null)
    val currentUserEmail: StateFlow<String?> = _currentUserEmail

    fun setCurrentUserEmail(email: String) {
        _currentUserEmail.value = email
    }
}
