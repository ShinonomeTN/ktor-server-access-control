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

    /**
     * Add a meta.
     *
     * Do nothing if [meta] is null
     */
    fun addMeta(meta : Any?) {
        this.meta.add(meta ?: return)
    }

    /**
     * Add a lot of metas
     *
     * @param metas list of meta, null values will be ignored.
     */
    fun addMeta(metas : Collection<Any?>) {
        this.meta.addAll(metas.filterNotNull().takeIf { it.isNotEmpty() } ?: return)
    }

    /**
     * Add a bunch of metas
     *
     * @param meta meta, null values will be ignored.
     */
    fun addMeta(vararg meta : Any?) {
        this.meta.addAll(meta.filterNotNull().takeIf { it.isNotEmpty() } ?: return)
    }

    /** Current request */
    val request : ApplicationRequest
}