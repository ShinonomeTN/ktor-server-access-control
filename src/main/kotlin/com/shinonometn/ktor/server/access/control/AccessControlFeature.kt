package com.shinonometn.ktor.server.access.control

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import org.slf4j.LoggerFactory
import java.util.*

class AccessControl(debug : Boolean, configuration: Configuration) {

    private val extractors: List<AccessControlMetaExtractor> = configuration.authorizationInfoProviders
    private val onUnauthorized: OnUnAuthorizedHandler = configuration.onUnAuthorized

    private val pipeline = AccessControlPipeline(debug)

    class Configuration {
        internal val authorizationInfoProviders = LinkedList<AccessControlMetaExtractor>()

        var debug : Boolean? = null

        internal var onUnAuthorized: OnUnAuthorizedHandler = {_, reason ->
            call.respond(HttpStatusCode.Forbidden, reason.toString())
        }

        @Deprecated("use provider(name, extractor) instead", ReplaceWith("provider(name, extractor)"), DeprecationLevel.ERROR)
        fun metaProvider(provider: suspend KtorCallContext.(AccessControlMetaProviderContext) -> Unit) {
            error("use provider(name, extractor) instead")
        }

        @Deprecated(
            "use addMetaExtractor(name, extractor) instead",
            ReplaceWith("addMetaExtractor(name, extractor)"),
            level = DeprecationLevel.ERROR
        )
        fun provider(name: String, contextCallExtractor: suspend KtorCallContext.(AccessControlMetaProviderContext) -> Unit) {
            error("use provider(name, extractor) instead")
        }

        /**
         * Register a meta extractor. If no [name] provided, it will register with name 'default'.
         *
         * If extractor named [name] already register, throw an error.
         */
        fun addMetaExtractor(name : String = "default", extractor: suspend AccessControlMetaProviderContext.() -> Unit) {
            require(authorizationInfoProviders.none { it.name == name }) { "Provider with name $name already registered." }
            authorizationInfoProviders.add(AccessControlMetaExtractor(name, extractor))
        }

        @Deprecated("use 'unauthorized' instead.", ReplaceWith("unauthorized {}"), DeprecationLevel.WARNING)
        fun onUnAuthorized(handler: OnUnAuthorizedHandler) {
            onUnAuthorized = handler
        }

        /**
         * Handle request when access control checker result returns 'Reject'.
         */
        fun doAfterReject(handler: OnUnAuthorizedHandler) { onUnAuthorized = handler }

        @Deprecated("use 'doAfterReject' instead", ReplaceWith("doAfterReject {}"), DeprecationLevel.WARNING)
        fun unauthenticated(handler: OnUnAuthorizedHandler) = doAfterReject(handler)
    }

    companion object Feature : ApplicationFeature<Application, Configuration, AccessControl> {
        override val key: AttributeKey<AccessControl> = AttributeKey("AccessControl")
        internal val logger = LoggerFactory.getLogger("AccessControlFeature")

        // Don't need it now
        // val AuthorizationPhase = PipelinePhase("Authorization")

        val AccessControlPhase = PipelinePhase("AccessControl")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): AccessControl {
            val configuration = Configuration().apply(configure)
            return AccessControl(configuration.debug ?: pipeline.developmentMode, configuration)
        }
    }

    fun interceptPipeline(routePipeline: Route, providerNames: Set<String>, checkers: List<AccessControlChecker>) {
        if (extractors.isEmpty()) logger.warn(
            "No provider configured. Checker for route '{}' may lack of info. Register an no-op extractor to hide this message.",
            routePipeline.toString()
        )

        val pipeline = routePipeline.application.feature(AccessControl).pipeline
        routePipeline.insertPhaseBefore(ApplicationCallPipeline.Call, AccessControlPhase)
        routePipeline.intercept(AccessControlPhase) {
            val extractors = if (providerNames.isEmpty()) extractors else extractors.filter { providerNames.contains(it.name) }

            // Get current access control context
            val context = call.attributes.computeIfAbsent(AccessControlContextImpl.AttributeKey) {
                AccessControlContextImpl(call.request, extractors, checkers)
            }

            val result = try {
                pipeline.execute(context)
                // Get process result from context
                context.attributes[AccessControlContextImpl.ProcessResultAttributeKey]
            } catch (e: Exception) {
                application.log.error("Error while processing AccessControl pipeline", e)
                throw e
            }

            if (result is AccessControlCheckerResult.Rejected) {
                // Do reject action
                onUnauthorized(context, result)
                finish()
            }
        }
    }
}

/**
 * Provide a convenience way to access meta snapshot of the
 * current application call
 */
val ApplicationCall.accessControl: AccessControlMetaSnapshot
    get() = attributes[AccessControlContextImpl.AttributeKey]