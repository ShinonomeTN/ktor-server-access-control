package com.shinonometn.ktor.server.access.control

/**
 * AccessControlMetaProvider provides information for access control checkers from request.
 */
interface AccessControlMetaExtractor {
    /** Name of this provider */
    val name : String

    /** Provider function */
    val extractor : suspend AccessControlMetaProviderContext.() -> Unit


    companion object {
        /** Create an AccessControlMetaProvider */
        operator fun invoke(name : String, extractor : suspend AccessControlMetaProviderContext.() -> Unit) = object : AccessControlMetaExtractor {
            override val name: String = name
            override val extractor: suspend AccessControlMetaProviderContext.() -> Unit = extractor
        }
    }
}