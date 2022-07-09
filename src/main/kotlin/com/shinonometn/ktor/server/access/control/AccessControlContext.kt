package com.shinonometn.ktor.server.access.control

/**
 * Mutable AccessControlContext
 */
interface AccessControlContext {
    val meta: MutableCollection<Any>
}