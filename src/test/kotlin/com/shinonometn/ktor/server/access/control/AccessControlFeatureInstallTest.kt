package com.shinonometn.ktor.server.access.control

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.server.testing.*
import org.junit.Test

class AccessControlFeatureInstallTest {
    @Test
    fun `Test install`() {
        withTestApplication(Application::moduleTestAppFeatureInstall) {  }
    }
}

/** The application for feature install testing */
internal fun Application.moduleTestAppFeatureInstall() {
    install(AccessControl) {
        addMetaProvider {  }
        doAfterReject { _, _ -> call.respond(HttpStatusCode.Forbidden) }
    }
}