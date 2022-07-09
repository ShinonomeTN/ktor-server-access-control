package com.shinonometn.ktor.server.access.control

import io.ktor.request.*

/**
 * AccessControlContext for meta providers
 */
interface AccessControlMetaProviderContext : AccessControlContext {

    @Deprecated("use addMeta() instead", ReplaceWith("addMeta()"))
    fun put(meta: Any) = addMeta(meta)

    @Deprecated("use addMeta() instead", ReplaceWith("addMeta()"))
    fun putAll(metas: Collection<Any>) = addMeta(metas)

    fun addMeta(meta : Any) = this.meta.add(meta)

    fun addMeta(metas : Collection<Any>) {
        if(metas.isNotEmpty()) this.meta.addAll(metas)
    }

    fun addMeta(vararg meta : Any) {
        if(meta.isNotEmpty()) this.meta.addAll(meta)
    }

    val request : ApplicationRequest
}