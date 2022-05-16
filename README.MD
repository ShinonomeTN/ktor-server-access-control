# ktor-server-access-control

Access control feature for Ktor Server

It is a very simple plugin for implementing access control feature in ktor application

## Usage

```kotlin
install(AccessControl) {
    provider("App Token") {
        val token = call.request.header["APP-TOKEN"] ?: return@provider
        it.put(token) // Put anything you want into AccessControlContext
    }
}

// In route config

routing {
    // If call satisfy some condition, accept it or reject
    // default behavior is reject, so accept() is must call.
    accessControl({ if (meta == meta<String>()) reject() else accept() }) {
        get {
            val accessControlContext = call.accessControl
            val token = accessControl.meta<String>()!!
            call.respond("Hello world! You have token $token")
        }
    }
}

```