package com.shinonometn.ktor.server.access.control

/**
 * Readonly AccessControl Meta, containing [meta]s that extracted.
 */
interface AccessControlMetaSnapshot {
    val meta: Collection<Any>
}