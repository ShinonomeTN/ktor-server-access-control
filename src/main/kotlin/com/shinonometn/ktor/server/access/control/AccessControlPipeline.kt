package com.shinonometn.ktor.server.access.control

import io.ktor.util.pipeline.*

class AccessControlPipeline(
    override val developmentMode: Boolean = false
) : Pipeline<Unit, AccessControlContextImpl>(MetaExtractPhase, CheckPhase) {
    init {
        intercept(MetaExtractPhase) { context.refreshMeta() }

        intercept(CheckPhase) {
            val result = context.evaluateChecker()
            context.attributes.put(AccessControlContextImpl.ProcessResultAttributeKey, result)
        }
    }

    companion object {
        val MetaExtractPhase = PipelinePhase("AccessControlMetaExtract")
        val CheckPhase = PipelinePhase("AccessControlCheck")
    }
}