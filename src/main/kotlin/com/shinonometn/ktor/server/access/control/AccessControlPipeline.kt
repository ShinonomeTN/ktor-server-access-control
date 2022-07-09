package com.shinonometn.ktor.server.access.control

import io.ktor.util.pipeline.*

class AccessControlPipeline(override val developmentMode: Boolean = false) : Pipeline<Unit, AccessControlContextImpl>(
    MetaExtractPhase,
    CheckPhase
) {
    init {
        initPipelineActions()
    }

    private fun initPipelineActions() {
        intercept(MetaExtractPhase) {
            val extractors = context.extractors
            extractors.forEach { it.extractor(context) }
        }

        intercept(CheckPhase) {
            val checkers = context.checkers
            for (checker in checkers) {
                checker.invoke(context)
                if(context.finished) break
            }
        }
    }

    companion object {
        val MetaExtractPhase = PipelinePhase("AccessControlMetaExtract")
        val CheckPhase = PipelinePhase("AccessControlCheck")

        val instance = AccessControlPipeline()
    }
}