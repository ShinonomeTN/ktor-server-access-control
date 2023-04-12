package com.shinonometn.ktor.server.access.control

/**
 * Represent an access control checker response.
 *
 * It only has two type : Passed(Success) or Rejected(Failed)
 */
sealed interface AccessControlCheckerResult {
    val isRejected : Boolean
        get() = this is Rejected

    class Passed internal constructor(val finish : Boolean) : AccessControlCheckerResult

    class Rejected internal constructor(val reason : String, val message : String) : AccessControlCheckerResult {
        override fun toString(): String = when {
            reason.isBlank() -> message
            message.isBlank() -> reason
            else -> "$reason, $message"
        }
    }
}