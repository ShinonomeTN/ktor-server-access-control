package com.shinonometn.ktor.server.access.control

/**
 * Provides information to handlers or application call.
 */
interface AccessControlContextSnapshot : AccessControlMetaSnapshot {

    /**
     * Get the reject reason of this access control
     * If the request not be rejected, returns null
     */
    fun rejectReason(): AccessControlCheckerResult.Rejected?

    /**
     * Get access control result.
     */
    fun result() : AccessControlCheckerResult
}