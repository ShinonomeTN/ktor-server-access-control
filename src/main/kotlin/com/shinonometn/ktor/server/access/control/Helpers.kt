package com.shinonometn.ktor.server.access.control

/**
 * Find metas with given type
 */
inline fun <reified T> AccessControlMetaSnapshot.metas() = meta.filterIsInstance<T>()

/**
 * Find first match meta with given type
 */
inline fun <reified T> AccessControlMetaSnapshot.meta() = meta.filterIsInstance<T>().firstOrNull()