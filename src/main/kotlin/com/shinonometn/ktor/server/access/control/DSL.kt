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
fun Route.accessControl(checker: suspend AccessControlCheckerContext.() -> Unit, builder: Route.() -> Unit): Route {
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

class AccessControlRequirement(providerNames: List<String>, val checkers: List<AccessControlChecker>) {
    val providerNames = providerNames.toSet()

    companion object {
        class Builder internal constructor(
            private val providers : MutableList<String> = mutableListOf(),
            private val checkers : MutableList<AccessControlChecker> = mutableListOf()
        ) {
            fun provider(providerName: String) = apply { providers.add(providerName) }

            fun provider(vararg providerNames: String) = apply { providers.addAll(providerNames) }

            fun checker(checker: AccessControlChecker) = apply { checkers.add(checker) }

            fun build() = AccessControlRequirement(providers.toList(), checkers.toList())
        }

        fun builder() = Builder()
    }
}