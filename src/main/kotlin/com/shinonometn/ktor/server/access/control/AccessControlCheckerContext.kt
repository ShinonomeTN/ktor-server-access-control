package com.shinonometn.ktor.server.access.control

/**
 * AccessControl context for checker. Providing basic method for checkers.
 * Metas are readonly.
 */
interface AccessControlCheckerContext : AccessControlMetaSnapshot {

    /**
     * Providing reject reason with [title] and [message]
     */
    fun reject(title: String, message: String)

    /**
     * Providing reject reason by a title-to-message pair
     */
    fun reject(vararg reasons: Pair<String, String>) = reasons.forEach { reject(it.first, it.second) }

    /**
     * Stop other checkers. Finishing current check pipeline.
     */
    fun finish()

    /**
     * Pass the request
     */
    fun accept()
}