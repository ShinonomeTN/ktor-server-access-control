package com.shinonometn.ktor.server.access.control

/**
 * Readonly AccessControl Meta, containing [meta]s that extracted.
 */
interface AccessControlMetaSnapshot {
    /** The context's meta bucket. Storing simple values */
    val meta: Collection<Any>
}