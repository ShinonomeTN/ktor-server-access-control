package com.shinonometn.ktor.server.access.control

interface AccessControlRequirement {
    /**
     * Meta providers required by this predicate.
     */
    val providerNames: Set<String>

    /**
    * Access control checker logics
    */
    val checker: AccessControlChecker

    companion object {
        /** Create an access control requirement */
        operator fun invoke(vararg providerName: String, checker: AccessControlChecker) = object : AccessControlRequirement {
            override val providerNames: Set<String> = providerName.toSet()
            override val checker: AccessControlChecker = checker
        }
    }
}