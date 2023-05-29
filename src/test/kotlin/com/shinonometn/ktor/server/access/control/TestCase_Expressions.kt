@file:Suppress("ClassName", "PrivatePropertyName")

package com.shinonometn.ktor.server.access.control

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertEquals

class TestCase_Expressions {
    private val HEADER_1 = "HEADER_FIRST"
    private val HEADER_2 = "HEADER_SECOND"

    private val exp1 = AccessControlRequirement { if(metas<String>().contains("token1")) accept() else reject() }
    private val exp2 = AccessControlRequirement { if(metas<String>().contains("token2")) accept() else reject() }
    private val exp3 = AccessControlRequirement { if(meta.isNotEmpty()) accept() else reject() }

    private val caseExpression : Application.() -> Unit = {
        install(AccessControl) {
            addMetaProvider {
                request.header(HEADER_1)?.let(::addMeta)
                request.header(HEADER_2)?.let(::addMeta)
            }
            doAfterReject { _, _ -> call.respond(HttpStatusCode.Forbidden) }
        }

        routing {
            accessControl(exp1 and exp2) {
                get("/case1") {
                    call.respond(HttpStatusCode.OK)
                }
            }

            accessControl(exp1 or exp2) {
                get("/case2") {
                    call.respond(HttpStatusCode.OK)
                }
            }

            accessControl(exp3.not()) {
                get("/case3") {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }

    @Test
    fun testAndLogic() {
        val status = withTestApplication(caseExpression) {
            handleRequest(HttpMethod.Get, "/case1"){
                addHeader(HEADER_1, "token1")
                addHeader(HEADER_2, "token2")
            }.response.status()
        }

        assertEquals(HttpStatusCode.OK, status)
    }

    @Test
    fun testOrLogic() {
        val status = withTestApplication(caseExpression) {
            handleRequest(HttpMethod.Get, "/case2") {
                addHeader(HEADER_2, "token2")
            }.response.status()
        }

        assertEquals(HttpStatusCode.OK, status)
    }

    @Test
    fun testNotLogic() {
        val status = withTestApplication(caseExpression) {
            handleRequest(HttpMethod.Get, "/case3").response.status()
        }

        assertEquals(HttpStatusCode.OK, status)
    }
}