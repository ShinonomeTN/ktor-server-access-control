package com.shinonometn.ktor.server.access.control

interface AccessControlRequirement {
    val providerNames: Set<String>
    val checkers: List<AccessControlChecker>

    companion object {
        class Builder internal constructor(
            private val providers: MutableList<String> = mutableListOf(),
            private val checkers: MutableList<AccessControlChecker> = mutableListOf()
        ) {
            fun provider(providerName: String) = apply { providers.add(providerName) }

            fun provider(vararg providerNames: String) = apply { providers.addAll(providerNames) }

            fun checker(checker: AccessControlChecker) = apply { checkers.add(checker) }

            fun build(): AccessControlRequirement = AccessControlRequirementImpl(providers.toSet(), checkers.toList())
        }

        fun builder() = Builder()
    }
}