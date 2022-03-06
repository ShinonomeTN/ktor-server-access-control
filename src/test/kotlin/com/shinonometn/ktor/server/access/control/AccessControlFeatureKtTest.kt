package com.shinonometn.ktor.server.access.control

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import org.junit.Assert.*
import org.junit.Test
import org.slf4j.LoggerFactory

fun Application.testInstallModule() {
    class UserInfoBean(val username : String)

    install(AccessControl) {
        metaProvider { c ->
            call.request.header("Test-Tag")?.takeIf { it.isNotBlank() }?.let { c.put(it) }
        }

        provider("UserInfo") { c ->
            call.request.cookies["user_info"]?.let { c.put(UserInfoBean(it)) }
        }
    }

    routing {
        get {
            call.respondText { "Hello World" }
        }

        accessControl({ if (true == meta<String>()?.isNotBlank()) accept() }) {
            get("/need_a_test_tag") {
                call.respondText { call.accessControl.meta<String>()!! }
            }
        }

        val needTagChecker : suspend AccessControlCheckerContext.() -> Unit = {
            if (true == meta<String>()?.isNotBlank()) accept() else reject("Message: ", "Need a Test-Tag Header.")
        }

        accessControl(needTagChecker) {
            get("/reject_has_message") {
                call.respondText { call.accessControl.meta<String>()!! }
            }
        }

        accessControl("UserInfo", checker = { if(meta<UserInfoBean>()?.username == "shinonometn") accept() }) {
            get("/has_user_info") {
                call.respond(call.accessControl.meta<UserInfoBean>()!!.username)
            }
        }
    }
}

class AccessControlFeatureKtTest {

    private val logger = LoggerFactory.getLogger("AccessControlFeatureKtTest")

    @Test
    fun `Test named access control`() {
        withTestApplication(Application::testInstallModule) {
            handleRequest(HttpMethod.Get, "/has_user_info") {
                addHeader("Cookie", "user_info=shinonometn")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("shinonometn", response.content)
                assertTrue(accessControl.meta.size == 1)
            }
        }
    }

    @Test
    fun `Test plugin install`() {
        withTestApplication(Application::testInstallModule) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals("Hello World", response.content)
            }
        }
    }

    @Test
    fun `Test if contains a meta`() {
        withTestApplication(Application::testInstallModule) {
            val tag = "Hello world!"

            handleRequest(HttpMethod.Get, "/need_a_test_tag") {
                addHeader("Test-Tag", tag)
            }.apply { assertEquals(tag, response.content) }
        }
    }

    @Test
    fun `Test reject when no meta`() {
        withTestApplication(Application::testInstallModule) {
            handleRequest(HttpMethod.Get, "/need_a_test_tag").apply {
                val responseStatus = response.status()
                val responseContent = response.content
                assertEquals(HttpStatusCode.Forbidden, responseStatus)
                assertNull(responseContent)
                logger.info("Test reject when no meta. Returns code '{}', content '{}'", responseStatus, responseContent)
            }
        }
    }

    @Test
    fun `Test reject has message`() {
        withTestApplication(Application::testInstallModule) {
            handleRequest(HttpMethod.Get, "/reject_has_message").apply {
                val responseStatus = response.status()
                val responseContent = response.content
                assertEquals(HttpStatusCode.Forbidden, responseStatus)
                assertNotNull(responseContent)
                logger.info("Test reject when no meta and has message. Returns code '{}', content '{}'", responseStatus, responseContent)
            }
        }
    }
}