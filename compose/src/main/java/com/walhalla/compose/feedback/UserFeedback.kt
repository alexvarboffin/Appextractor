package com.walhalla.compose.feedback

/**
 * Shared user-facing action result — phone snackbar / TV banner both consume this.
 * Kind drives [MaterialTheme] / [androidx.tv.material3.MaterialTheme] roles (no hardcoded RGB).
 */
data class UserFeedback(
    val message: String,
    val kind: Kind = Kind.Info
) {
    enum class Kind {
        /** Completed successfully */
        Success,
        /** Failed or blocked */
        Error,
        /** In-progress / neutral acknowledgment */
        Info
    }
}
