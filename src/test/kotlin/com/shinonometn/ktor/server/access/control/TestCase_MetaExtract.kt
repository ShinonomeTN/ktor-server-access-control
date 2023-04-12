@file:Suppress("ClassName")

package com.shinonometn.ktor.server.access.control

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import org.junit.Assert
import org.junit.Test

class TestCase_MetaExtract {
    private val headerName = "X_TEST_HEADER"
    private val headerValue = "ktor-access-control"

    private val caseMetaExtractor : Application.() -> Unit = {
        install(AccessControl) {
            addMetaProvider { addMeta(request.header(headerName)) }
        }

        routing {
            accessControl(AccessControlRequirement { accept() }) {
                get("/test0") { call.respond(HttpStatusCode.OK) }
            }
        }
    }

    @Test
    fun `Test extract meta and access control checking`() {
        withTestApplication(caseMetaExtractor) {
            val metaValue = handleRequest(HttpMethod.Get, "/test0") { addHeader(headerName, headerValue) }.accessControl.meta<String>()
            Assert.assertEquals("Meta value should equals header value", headerValue, metaValue)
        }
    }
}