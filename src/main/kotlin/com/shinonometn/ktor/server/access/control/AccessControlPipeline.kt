package com.shinonometn.ktor.server.access.control

import io.ktor.util.pipeline.*

class AccessControlPipeline(
    override val developmentMode: Boolean = false
) : Pipeline<Unit, AccessControlContextImpl>(MetaExtractPhase, CheckPhase) {
    init {
        intercept(MetaExtractPhase) {
            val extractors = context.extractors
            extractors.forEach { it.extractor(context) }
        }

        intercept(CheckPhase) {
            val checkers = context.checkers
            for (checker in checkers) {
                val result = checker(context)
                context.attributes.put(AccessControlContextImpl.ProcessResultAttributeKey, result)
                if (result.isRejected) break
                if (result is AccessControlCheckerResult.Passed && result.finish) break
            }
        }
    }

    companion object {
        val MetaExtractPhase = PipelinePhase("AccessControlMetaExtract")
        val CheckPhase = PipelinePhase("AccessControlCheck")
    }
}