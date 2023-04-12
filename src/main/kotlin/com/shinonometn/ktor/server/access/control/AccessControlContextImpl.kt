package com.shinonometn.ktor.server.access.control

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.util.*
import java.util.*

class AccessControlContextImpl(
    override val request: ApplicationRequest,
    internal val extractors: List<AccessControlMetaExtractor>,
    internal val checkers: List<AccessControlChecker>
) : AccessControlMetaProviderContext, AccessControlCheckerContext, AccessControlContextSnapshot {

    override val meta: MutableCollection<Any> by lazy {
        attributes.computeIfAbsent(MetaBucketAttributeKey) { LinkedList() }
    }

    override val attributes: Attributes by lazy { Attributes() }

    override val application: Application
        get() = request.call.application

    override fun rejectReason(): AccessControlCheckerResult.Rejected? {
        val result = attributes.getOrNull(ProcessResultAttributeKey) ?: return null
        if (result is AccessControlCheckerResult.Rejected) return result
        return null
    }

    companion object {
        internal val AttributeKey = AttributeKey<AccessControlContextImpl>("AccessControlContext")

        internal val MetaBucketAttributeKey = AttributeKey<MutableCollection<Any>>("MetaBucket")
        internal val ProcessResultAttributeKey = AttributeKey<AccessControlCheckerResult>("ProcessResult")
    }
}