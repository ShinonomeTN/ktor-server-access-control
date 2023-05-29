package com.shinonometn.ktor.server.access.control

import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*


/**
 * Add access control route.
 * Use the given [checker] to decides what requests to child routes will be accepted.
 * Default behavior is rejecting all request. To allow a request, the `accept()` in checker context must be call.
 */
@ContextDsl
fun Route.accessControl(checker: AccessControlChecker, builder: Route.() -> Unit): Route {
    val authorizationRoute = createChild(AccessControlRouteSelector())
    application.feature(AccessControl).interceptPipeline(authorizationRoute, emptySet(), checker)
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
    application.feature(AccessControl).interceptPipeline(authorizationRoute, requirement.providerNames, requirement.checker)
    authorizationRoute.builder()
    return authorizationRoute
}


/** Combine two AccessControlRequirement with AND logic */
infix fun AccessControlRequirement.and(later : AccessControlRequirement) : AccessControlRequirement =
    AccessControlRequirementImpl(providerNames + later.providerNames) {
        val left = checker()
        when {
            left.isRejected -> left
            else -> with(later) { checker() }
        }
    }

/** Combine two AccessControlRequirement with OR logic */
infix fun AccessControlRequirement.or(later : AccessControlRequirement) : AccessControlRequirement =
    AccessControlRequirementImpl(providerNames + later.providerNames) {
        val left = checker()
        when {
            !left.isRejected -> left
            else -> with(later) { checker() }
        }
    }

/** Reverse an AccessControlRequirement */
fun AccessControlRequirement.not(reason : String = "", message : String = "") : AccessControlRequirement =
    AccessControlRequirementImpl(providerNames) {
        val result = checker()
        if(result.isRejected) accept()
        else reject(reason, message)
    }