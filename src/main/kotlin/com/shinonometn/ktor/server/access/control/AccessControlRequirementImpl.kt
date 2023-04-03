package com.shinonometn.ktor.server.access.control

internal class AccessControlRequirementImpl(override val providerNames: Set<String>, override val checkers: List<AccessControlChecker>) :
    AccessControlRequirement