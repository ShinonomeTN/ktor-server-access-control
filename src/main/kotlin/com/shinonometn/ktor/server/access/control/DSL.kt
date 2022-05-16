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
fun Route.accessControl(vararg providerNames: String, checker: suspend AccessControlCheckerContext.() -> Unit, builder: Route.() -> Unit): Route {
    val authorizationRoute = createChild(AccessControlRouteSelector())
    application.feature(AccessControl).interceptPipeline(providerNames.toSet(), authorizationRoute, checker)
    authorizationRoute.builder()
    return authorizationRoute
}

/**
 * Add access control route.
 * Use the given [checker] to decides what requests to child routes will be accepted.
 * Default behavior is rejecting all request. To allow a request, the `accept()` in checker context must be call.
 */
@ContextDsl
fun Route.accessControl(checker: suspend AccessControlCheckerContext.() -> Unit, builder: Route.() -> Unit): Route {
    val authorizationRoute = createChild(AccessControlRouteSelector())
    application.feature(AccessControl).interceptPipeline(emptySet(), authorizationRoute, checker)
    authorizationRoute.builder()
    return authorizationRoute
}

/**
 * Add access control route.
 * Use the given [requirement] to decides what data should be provided and what requests to child routes will be accepted.
 * Default behavior is rejecting all request. To allow a request, the `accept()` in checker context must be call.
 */
@ContextDsl
fun Route.accessControl(requirement : AccessControlRequirement, builder: Route.() -> Unit): Route {
    val authorizationRoute = createChild(AccessControlRouteSelector())
    application.feature(AccessControl).interceptPipeline(requirement.providerNames, authorizationRoute, requirement.checker)
    authorizationRoute.builder()
    return authorizationRoute
}

class AccessControlRequirement(vararg providerNames : String, val checker : AccessControlChecker) {
    val providerNames = providerNames.toSet()
    companion object {
        fun default(checker : AccessControlChecker) : AccessControlRequirement {
            return AccessControlRequirement(checker = checker)
        }
    }
}