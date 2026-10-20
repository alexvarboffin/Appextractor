package com.walhalla.compose.feedback

import com.walhalla.compose.log.AppLogBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Cross-ViewModel UI feedback (Extractor + AppDetail + …).
 * UI hosts collect [feedback]; theme colors chosen from [UserFeedback.Kind].
 */
object ActionFeedbackBus {
    private val _feedback = MutableStateFlow<UserFeedback?>(null)
    val feedback: StateFlow<UserFeedback?> = _feedback.asStateFlow()

    fun post(message: String, kind: UserFeedback.Kind = UserFeedback.Kind.Info) {
        _feedback.value = UserFeedback(message, kind)
        when (kind) {
            UserFeedback.Kind.Error -> AppLogBus.error(message)
            UserFeedback.Kind.Success, UserFeedback.Kind.Info -> AppLogBus.success(message)
        }
    }

    fun clear() {
        _feedback.value = null
    }
}
