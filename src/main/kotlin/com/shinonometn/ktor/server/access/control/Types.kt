package com.shinonometn.ktor.server.access.control

import io.ktor.application.*
import io.ktor.util.pipeline.*

/**
 * Access Control Checker
 */
typealias AccessControlChecker = suspend AccessControlCheckerContext.() -> AccessControlCheckerResult

internal typealias KtorCallContext = PipelineContext<Unit, ApplicationCall>

internal typealias OnUnAuthorizedHandler = suspend KtorCallContext.(context: AccessControlContextSnapshot, reason: AccessControlCheckerResult.Rejected) -> Unit