package com.shinonometn.ktor.server.access.control

import io.ktor.routing.*

/**
 * Route selector implement of AccessControl
 */
class AccessControlRouteSelector : RouteSelector(RouteSelectorEvaluation.qualityTransparent) {

    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation(true, RouteSelectorEvaluation.qualityTransparent)
    }

    override fun toString(): String = "(access control)"
}