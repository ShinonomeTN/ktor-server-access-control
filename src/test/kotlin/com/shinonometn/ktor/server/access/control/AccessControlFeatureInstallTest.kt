package com.shinonometn.ktor.server.access.control

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.server.testing.*
import org.junit.Test

/**
 * Test case : Feature installation
 */
class AccessControlFeatureInstallTest {

    private val app : Application.() -> Unit = {
        install(AccessControl) {
            addMetaProvider {  }
            doAfterReject { _, _ -> call.respond(HttpStatusCode.Forbidden) }
        }
    }

    @Test
    fun `Test install`() {
        withTestApplication(app) {  }
    }
}