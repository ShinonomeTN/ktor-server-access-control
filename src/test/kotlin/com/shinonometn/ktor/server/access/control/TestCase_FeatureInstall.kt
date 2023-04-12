@file:Suppress("ClassName")

package com.shinonometn.ktor.server.access.control

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Test case : Feature installation
 */
class TestCase_FeatureInstall {

    private val app : Application.() -> Unit = {
        install(AccessControl) {
            addMetaProvider {  }
            doAfterReject { _, _ -> call.respond(HttpStatusCode.Forbidden) }
        }

        routing {
            get { call.respondText { "Hello World" } }
        }
    }

    @Test
    fun `Test install`() {
        val content = withTestApplication(app) {
            handleRequest(HttpMethod.Get, "/").response.content
        }

        assertEquals("Hello World", content)
    }
}