package com.shinonometn.ktor.server.access.control

import io.ktor.request.*
import io.ktor.util.*
import java.util.*

class AccessControlContextImpl(
    override val request: ApplicationRequest,
    internal val extractors : List<AccessControlMetaExtractor>,
    internal val checkers : List<AccessControlChecker>
) : AccessControlMetaProviderContext, AccessControlCheckerContext, AccessControlContextSnapshot {

    override val meta: MutableCollection<Any> by lazy { LinkedList() }

    private val rejectReasons: MutableMap<String, String> by lazy { HashMap() }

    override var isRejected: Boolean = true
        private set

    internal var finished = false

    override fun finish() {
        finished = true
    }

    override fun rejectReasons(): Map<String, String> {
        return rejectReasons.filterKeys { it.isNotEmpty() }
    }

    override fun accept() {
        isRejected = false
    }

    override fun reject(title: String, message: String) {
        isRejected = true
        rejectReasons[title] = message
    }

    companion object {
        internal val AttributeKey = AttributeKey<AccessControlContextImpl>("AccessControlContext")
    }
}