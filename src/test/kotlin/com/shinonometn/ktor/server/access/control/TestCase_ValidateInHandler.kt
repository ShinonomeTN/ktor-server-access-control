@file:Suppress("ClassName")

package com.shinonometn.ktor.server.access.control

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class TestCase_ValidateInHandler {
    private val expAcceptAll = AccessControlRequirement { accept() }

    private val caseValidateInHandler: Application.() -> Unit = {

        install(AccessControl) {
            addMetaProvider { request.header("X-HEADER")?.let(::addMeta) }
        }

        routing {
            accessControl(expAcceptAll) {
                val expInPlace = AccessControlRequirement { if (meta<String>() != null) accept() else reject() }
                get("/call_me_with_header") {
                    val context = call.checkAccessControl(expInPlace)
                    if (context.result() is AccessControlCheckerResult.Passed)
                        call.respond(HttpStatusCode.OK, context.meta<String>()!!)
                    else call.respond(HttpStatusCode.Forbidden)
                }
            }
        }
    }

    @Test
    fun testValidateInPlace() {
        withTestApplication(caseValidateInHandler) {
            val statement = handleRequest(HttpMethod.Get, "/call_me_with_header") {
                addHeader("X-HEADER", "Hello")
            }

            assertEquals(HttpStatusCode.OK, statement.response.status())
            assertEquals("Hello", statement.response.content)
        }
    }
}