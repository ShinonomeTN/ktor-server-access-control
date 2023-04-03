package com.shinonometn.ktor.server.access.control

import io.ktor.util.*

/**
 * Readonly AccessControl Meta, containing [meta]s that extracted.
 */
interface AccessControlMetaSnapshot {
    val meta: Collection<Any>
    val attributes: Attributes
}