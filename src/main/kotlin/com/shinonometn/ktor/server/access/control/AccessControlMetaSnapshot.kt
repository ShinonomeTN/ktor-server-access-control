package com.shinonometn.ktor.server.access.control

/**
 * Readonly AccessControl Meta, containing [meta]s that extracted.
 */
interface AccessControlMetaSnapshot {
    /** The context's meta bucket. Storing simple values */
    val meta: Collection<Any>
}

/**
 * Find metas with given type
 */
inline fun <reified T> AccessControlMetaSnapshot.metas() = meta.filterIsInstance<T>()

/**
 * Find first match meta with given type
 */
inline fun <reified T> AccessControlMetaSnapshot.meta() = meta.filterIsInstance<T>().firstOrNull()