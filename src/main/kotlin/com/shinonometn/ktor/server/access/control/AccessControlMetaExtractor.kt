package com.shinonometn.ktor.server.access.control

/**
 * Request Meta Extractor
 */
class AccessControlMetaExtractor(val name : String, internal val extractor : suspend AccessControlMetaProviderContext.() -> Unit)