# ktor-server-access-control

[![Deployment](https://github.com/ShinonomeTN/ktor-server-access-control/actions/workflows/deploy-shinonometn.yml/badge.svg)](https://github.com/ShinonomeTN/ktor-server-access-control/actions/workflows/deploy-shinonometn.yml)
[![Test](https://github.com/ShinonomeTN/ktor-server-access-control/actions/workflows/test-maven.yml/badge.svg)](https://github.com/ShinonomeTN/ktor-server-access-control/actions/workflows/test-maven.yml)
[![Version](https://img.shields.io/github/v/release/ShinonomeTN/ktor-server-access-control?include_prereleases)](https://github.com/ShinonomeTN/ktor-server-access-control/releases)
[![License](https://img.shields.io/github/license/ShinonomeTN/ktor-server-access-control)](https://github.com/ShinonomeTN/ktor-server-access-control/blob/master/LICENSE)

---

Access control feature for Ktor Server

It is a very simple plugin for implementing access control feature in ktor application

## Usage

For repository url, please see [ShinonomeTN Public Maven Repository](https://github.com/ShinonomeTN/maven-public)

Maven:
```xml
<dependency>
    <groupId>com.shinonometn</groupId>
    <artifactId>ktor-server-access-control</artifactId>
    <version>${release-or-snapshot-version}</version>
</dependency>
```

### A simple example

```kotlin
install(AccessControl) {
    addMetaProvider("App Token") {
        val token = call.request.header["APP-TOKEN"] ?: return@provider
        it.put(token) // Put anything you want into AccessControlContext
    }
}

// In route config

routing {
    // If call satisfy some condition, accept it or reject
    // default behavior is reject, so accept() is must call.
    accessControl({ if (meta == meta<String>()) accept() }) {
        get {
            val accessControlContext = call.accessControl
            val token = accessControl.meta<String>()!!
            call.respond("Hello world! You have token $token")
        }
    }
}

```

### Customizing unauthorized response

```kotlin
install(AccessControl) {
    unauthorized {
        // Get reject reasons from AccessControlContext
        val reasons : Map<String,String> = rejectReasons()
        
        call.respond(HttpStatusCode.Unauthorized, reasons)
    }
}
```