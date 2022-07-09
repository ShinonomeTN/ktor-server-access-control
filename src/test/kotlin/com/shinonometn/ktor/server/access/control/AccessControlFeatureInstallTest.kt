package com.shinonometn.ktor.server.access.control

import io.ktor.application.*
import io.ktor.server.testing.*
import org.junit.Test

fun Application.moduleTestAppFeatureInstall() {
    install(AccessControl) {

    }
}

class AccessControlFeatureInstallTest {
    @Test
    fun `Test install`() {
        withTestApplication(Application::moduleTestAppFeatureInstall) {  }
    }
}