# ktor-server-access-control

[![Deployment](https://github.com/ShinonomeTN/ktor-server-access-control/actions/workflows/release-and-deploy.yml/badge.svg)](https://github.com/ShinonomeTN/ktor-server-access-control/actions/workflows/release-and-deploy.yml)
[![Test](https://github.com/ShinonomeTN/ktor-server-access-control/actions/workflows/test-maven.yml/badge.svg)](https://github.com/ShinonomeTN/ktor-server-access-control/actions/workflows/test-maven.yml)
[![Version](https://img.shields.io/github/v/release/ShinonomeTN/ktor-server-access-control?include_prereleases)](https://github.com/ShinonomeTN/ktor-server-access-control/releases)
[![License](https://img.shields.io/github/license/ShinonomeTN/ktor-server-access-control)](https://github.com/ShinonomeTN/ktor-server-access-control/blob/master/LICENSE)

Access control feature for Ktor Server

It is a very simple plugin for implementing access control feature in ktor application

## Usage

Currently it only available in our private maven repository. You can add this to your repository list.

```
https://nexus.shinonometn.com/repository/maven-public/
```

A simple example:

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