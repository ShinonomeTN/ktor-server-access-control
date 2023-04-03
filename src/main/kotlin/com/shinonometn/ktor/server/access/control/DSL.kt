package com.shinonometn.ktor.server.access.control

import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*

/**
 * Add access control route.
 * Use the given [checker] to decides what requests to child routes will be accepted.
 * If provider names are empty, all providers will be used.
 * Default behavior is rejecting all request. To allow a request, the `accept()` in checker context must be call.
 */
@Deprecated("Deprecated")
@ContextDsl
fun Route.accessControl(vararg providerNames: String, checker: AccessControlChecker, builder: Route.() -> Unit): Route {
    val authorizationRoute = createChild(AccessControlRouteSelector())
    application.feature(AccessControl).interceptPipeline(authorizationRoute, providerNames.toSet(), listOf(checker))
    authorizationRoute.builder()
    return authorizationRoute
}

/**
 * Add access control route.
 * Use the given [checker] to decides what requests to child routes will be accepted.
 * Default behavior is rejecting all request. To allow a request, the `accept()` in checker context must be call.
 */
@ContextDsl
fun Route.accessControl(checker: AccessControlChecker, builder: Route.() -> Unit): Route {
    val authorizationRoute = createChild(AccessControlRouteSelector())
    application.feature(AccessControl).interceptPipeline(authorizationRoute, emptySet(), listOf(checker))
    authorizationRoute.builder()
    return authorizationRoute
}

/**
 * Add access control route.
 * Use the given [requirement] to decides what data should be provided and what requests to child routes will be accepted.
 * Default behavior is rejecting all request. To allow a request, the `accept()` in checker context must be call.
 */
@ContextDsl
fun Route.accessControl(requirement: AccessControlRequirement, builder: Route.() -> Unit): Route {
    val authorizationRoute = createChild(AccessControlRouteSelector())
    application.feature(AccessControl).interceptPipeline(authorizationRoute, requirement.providerNames, requirement.checkers)
    authorizationRoute.builder()
    return authorizationRoute
}