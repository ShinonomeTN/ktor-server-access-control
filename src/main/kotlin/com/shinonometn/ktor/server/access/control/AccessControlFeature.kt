package com.shinonometn.ktor.server.access.control

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import org.slf4j.LoggerFactory
import java.util.*

internal typealias CallContext = PipelineContext<Unit, ApplicationCall>

typealias AccessControlChecker = suspend AccessControlCheckerContext.() -> Unit

class AccessControlMetaExtractor(val name: String, val extractor: suspend CallContext.(AccessControlMetaProviderContext) -> Unit)

private typealias OnUnAuthorizedHandler = suspend CallContext.(AccessControlContextSnapshot) -> Unit

class AccessControl(configuration: Configuration) {

    private val providers: List<AccessControlMetaExtractor> = configuration.authorizationInfoProviders
    private val onUnauthorized: OnUnAuthorizedHandler = configuration.onUnAuthorized

    class Configuration {
        internal val authorizationInfoProviders = LinkedList<AccessControlMetaExtractor>()

        internal var onUnAuthorized: OnUnAuthorizedHandler = { c ->
            val reasons = c.rejectReasons()
            if (reasons.isNotEmpty()) call.respond(HttpStatusCode.Forbidden, reasons.entries.joinToString("\n") { it.key + it.value })
            else call.respond(HttpStatusCode.Forbidden)
        }

        @Deprecated("use provider(name, extractor) instead", ReplaceWith("provider(name, extractor)"))
        fun metaProvider(provider: suspend CallContext.(AccessControlMetaProviderContext) -> Unit) {
            provider("", provider)
        }

        fun provider(name: String, extractor: suspend CallContext.(AccessControlMetaProviderContext) -> Unit) {
            if (authorizationInfoProviders.any { it.name == name }) {
                throw IllegalArgumentException("Provider with name $name already registered.")
            }
            authorizationInfoProviders.add(AccessControlMetaExtractor(name, extractor))
        }

        fun onUnAuthorized(handler: OnUnAuthorizedHandler) {
            onUnAuthorized = handler
        }
    }

    companion object Feature : ApplicationFeature<Application, Configuration, AccessControl> {
        override val key: AttributeKey<AccessControl> = AttributeKey("AccessControl")
        internal val logger = LoggerFactory.getLogger("AccessControlFeature")

        // Don't need it now
        // val AuthorizationPhase = PipelinePhase("Authorization")

        val AccessControlPhase = PipelinePhase("AccessControl")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): AccessControl {
            val configuration = Configuration().apply(configure)
            return AccessControl(configuration)
        }

        fun buildContext(provider : AccessControlMetaProviderContext.() -> Unit) : AccessControlCheckerContext =
            AccessControlContextImpl().apply(provider)
    }

    fun interceptPipeline(providerNames: Set<String>, routePipeline: Route, checker: AccessControlChecker) {
        if (providers.isEmpty()) {
            logger.warn("No provider configured. Access control for route '{}' will not be installed.", routePipeline.toString())
            return
        }

        routePipeline.insertPhaseBefore(ApplicationCallPipeline.Call, AccessControlPhase)
        routePipeline.intercept(AccessControlPhase) {
            val context = AccessControlContextImpl.from(call)
            (if (providerNames.isEmpty()) providers else providers.filter { providerNames.contains(it.name) })
                .forEach { it.extractor(this, context) }

            checker(context)

            if (!context.isRejected) return@intercept

            onUnauthorized(context)

            finish()
        }
    }
}

/**
 * Provide a convenience way to access meta snapshot of the
 * current application call
 */
val ApplicationCall.accessControl: AccessControlMetaSnapshot
    get() = AccessControlContextImpl.from(this)

/**
 * Route selector implement of AccessControl
 */
class AccessControlRouteSelector : RouteSelector(RouteSelectorEvaluation.qualityTransparent) {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation(true, RouteSelectorEvaluation.qualityTransparent)
    }

    override fun toString(): String = "(access control)"
}

/**
 * Mutable AccessControlContext
 */
interface AccessControlContext {
    val meta: MutableCollection<Any>
}

/**
 * AccessControlContext for meta providers
 */
interface AccessControlMetaProviderContext : AccessControlContext {
    fun put(meta: Any) = this.meta.add(meta)
    fun putAll(metas: Collection<Any>) = this.meta.addAll(metas)
}

/**
 * Readonly AccessControl Meta, containing [meta]s that extracted.
 */
interface AccessControlMetaSnapshot {
    val meta: Collection<Any>
}

/**
 * AccessControl context for checker. Providing basic method for checkers.
 * Metas are readonly.
 */
interface AccessControlCheckerContext : AccessControlMetaSnapshot {

    /**
     * Providing reject reason with [title] and [message]
     */
    fun reject(title: String, message: String)

    /**
     * Providing reject reason by a title-to-message pair
     */
    fun reject(vararg reasons: Pair<String, String>) = reasons.forEach { reject(it.first, it.second) }

    /**
     * Pass the request
     */
    fun accept()
}

/**
 * Provides information to handlers or application call.
 */
interface AccessControlContextSnapshot : AccessControlMetaSnapshot {
    fun rejectReasons(): Map<String, String>
    val isRejected : Boolean
}

class AccessControlContextImpl : AccessControlMetaProviderContext, AccessControlCheckerContext, AccessControlContextSnapshot {
    override val meta: MutableCollection<Any> by lazy { LinkedList() }
    private val rejectReasons: MutableMap<String, String> by lazy { HashMap() }
    override var isRejected: Boolean = true
        private set

    override fun rejectReasons(): Map<String, String> {
        isRejected = true
        return rejectReasons.filterKeys { it.isNotEmpty() }
    }

    override fun accept() {
        isRejected = false
    }

    override fun reject(title: String, message: String) {
        isRejected = true
        rejectReasons[title] = message
    }

    companion object {
        private val AttributeKey = AttributeKey<AccessControlContextImpl>("AccessControlContext")

        fun from(call: ApplicationCall) = call.attributes.computeIfAbsent(AttributeKey) { AccessControlContextImpl() }
    }
}

