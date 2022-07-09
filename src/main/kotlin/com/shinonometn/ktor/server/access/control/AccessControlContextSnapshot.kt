package com.shinonometn.ktor.server.access.control

/**
 * Provides information to handlers or application call.
 */
interface AccessControlContextSnapshot : AccessControlMetaSnapshot {
    fun rejectReasons(): Map<String, String>

    val isRejected : Boolean
}