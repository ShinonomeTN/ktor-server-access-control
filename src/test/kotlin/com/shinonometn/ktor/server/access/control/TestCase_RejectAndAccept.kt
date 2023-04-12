@file:Suppress("ClassName", "PrivatePropertyName")

package com.shinonometn.ktor.server.access.control

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import org.junit.Assert
import org.junit.Test

class TestCase_RejectAndAccept {
    private val caseRejectAndAccept : Application.() -> Unit = {
        install(AccessControl) {
            addMetaProvider {  }
            doAfterReject { _, _ -> call.respond(HttpStatusCode.Forbidden) }
        }

        routing {
            accessControl(AccessControlRequirement { accept() }) {
                get("/accept") { call.respond(HttpStatusCode.OK) }
            }

            accessControl(AccessControlRequirement { reject() }) {
                get("/reject") { call.respond(HttpStatusCode.OK) }
            }
        }
    }
    @Test
    fun `Test reject and accept`() = withTestApplication(caseRejectAndAccept) {
        val rejectedStatusCode = handleRequest(HttpMethod.Get, "/reject").response.status()
        Assert.assertEquals("Request to /reject should returns 403", HttpStatusCode.Forbidden, rejectedStatusCode)

        val acceptStatusCode = handleRequest(HttpMethod.Get, "/accept").response.status()
        Assert.assertEquals("Request to /accept should returns 200", HttpStatusCode.OK, acceptStatusCode)
    }
}