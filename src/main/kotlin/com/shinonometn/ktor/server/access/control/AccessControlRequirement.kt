package com.shinonometn.ktor.server.access.control

interface AccessControlRequirement {
    /**
     * Meta providers required by this predicate.
     */
    val providerNames: Set<String>

    /**
    * Access control checker logics
    */
    val checkers: List<AccessControlChecker>

    companion object {
        /** Create an access control requirement */
        operator fun invoke(vararg providerName: String, checker: AccessControlChecker) = object : AccessControlRequirement {
            override val providerNames: Set<String> = providerName.toSet()
            override val checkers: List<AccessControlChecker> = listOf(checker)
        }

        @Deprecated("Builder deprecated")
        class Builder internal constructor(
            private val providers: MutableList<String> = mutableListOf(),
            private val checkers: MutableList<AccessControlChecker> = mutableListOf()
        ) {
            fun provider(providerName: String) = apply { providers.add(providerName) }

            fun provider(vararg providerNames: String) = apply { providers.addAll(providerNames) }

            fun checker(checker: AccessControlChecker) = apply { checkers.add(checker) }

            fun build(): AccessControlRequirement = AccessControlRequirementImpl(providers.toSet(), checkers.toList())
        }

        @Deprecated("Builder deprecated", ReplaceWith("AccessControlRequirement()") ,level = DeprecationLevel.WARNING)
        fun builder() = Builder()
    }
}