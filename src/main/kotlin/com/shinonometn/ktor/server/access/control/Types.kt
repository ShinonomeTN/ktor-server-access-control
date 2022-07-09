package com.shinonometn.ktor.server.access.control

import io.ktor.application.*
import io.ktor.util.pipeline.*

internal typealias KtorCallContext = PipelineContext<Unit, ApplicationCall>

internal typealias OnUnAuthorizedHandler = suspend KtorCallContext.(AccessControlContextSnapshot) -> Unit