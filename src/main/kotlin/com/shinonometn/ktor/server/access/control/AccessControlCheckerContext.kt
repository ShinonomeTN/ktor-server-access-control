package com.shinonometn.ktor.server.access.control

import io.ktor.application.*

/**
 * AccessControl context for checker. Providing basic method for checkers.
 * Metas are readonly.
 */
interface AccessControlCheckerContext : AccessControlMetaSnapshot {

    /** current application */
    val application: Application

    /**
     * Reject the request.
     *
     * Reject means the processing chain will be finished.
     * A reason and message are optional. But tell the user why is better.
     */
    fun reject(reason: String = "", message: String = "") : AccessControlCheckerResult =
        AccessControlCheckerResult.Rejected(reason, message)

    /**
     * Pass the request.
     *
     * Pass the request does not mean the processing chain [finished],
     * But for convenience, [finished] is true by default.
     */
    fun accept(finished : Boolean = true) : AccessControlCheckerResult =
        AccessControlCheckerResult.Passed(finished)
}