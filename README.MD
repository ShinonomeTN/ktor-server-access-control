# ktor-server-access-control

[![Deployment](https://github.com/ShinonomeTN/ktor-server-access-control/actions/workflows/deploy-shinonometn.yml/badge.svg)](https://github.com/ShinonomeTN/ktor-server-access-control/actions/workflows/deploy-shinonometn.yml)
[![Test](https://github.com/ShinonomeTN/ktor-server-access-control/actions/workflows/test-maven.yml/badge.svg)](https://github.com/ShinonomeTN/ktor-server-access-control/actions/workflows/test-maven.yml)
[![Version](https://img.shields.io/github/v/release/ShinonomeTN/ktor-server-access-control?include_prereleases)](https://github.com/ShinonomeTN/ktor-server-access-control/releases)
[![License](https://img.shields.io/github/license/ShinonomeTN/ktor-server-access-control)](https://github.com/ShinonomeTN/ktor-server-access-control/blob/master/LICENSE)

---

Access control feature for Ktor Server

It is a very simple plugin for implementing access control feature in ktor application.

### About ktor 2.0+

This is a legacy project for our old applications using ktor 1.6.8. 
We will refactor ktor-server-access-control in a new repository for ktor 2.0+ in the future.

## Usage

Please see [ShinonomeTN Public Maven Repository](https://github.com/ShinonomeTN/maven-public) for repository urls and repository manual.

Maven:
```xml
<dependency>
    <groupId>com.shinonometn</groupId>
    <artifactId>ktor-server-access-control</artifactId>
    <version>${release-version}</version>
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
    // If call satisfy some condition, accept it, or not, reject.
    accessControl({ if (meta == meta<String>()) accept() else reject() }) {
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

### Reusing expressions
`accessControl()` takes a lambda as it's first parameter. You can create and save it for later use, 
or event combining those lambda functions. 
```kotlin
val requireSession = AccessControlChecker { if(meta<UserSession>() != null) accepe() else reject() }
val requireAdmin = AccessControlChecker { if(isAdmin()) accept() else reject() }
val requireLoggedIn = AccessControlChecker { if(isSessionLoggedIn()) accept() else reject() }
    
routing {
    accessControl(requireSession or requireLoggedIn) {
        get("/homepage") {
            call.respond("Hello User!")
        }
    }
    
    accessControl(requireSession and requireAdmin) {
        get("/admin") {
            call.respond("Hello admin!")
        }
    }
    
    accessControl(requireLoggedIn.not()) {
        post("/logout") {
            call.respond("You are logged out.")
        }
    }
}
```

### AccessControlMetaSnapshot 

Information of AccessControlContext can access via interface `AccessControlMetaSnapshot`.
You can add extension function to do some useful things such as check user session state.

```kotlin
fun AccessControlMetaSnapshot.isUserLoggedIn() : Boolean {
    val session = metas<UserSession>() ?: return false
    return session.isLoggedIn()
}

val requireLoggedIn = AccessControlChecker { if(isUserLoggedIn()) accept() else reject() }

routing {
    accessControl(requireLoggedIn) {
        get("/homepage") {
            call.respond("Hello User!")
        }
    }
}

```

### Validate access control in-place

You can access `checkAccessControl()` from ApplicationCall and do additional checks for permissions.

```kotlin
val requireAdmin = AccessControlChecker { if(isAdmin()) accept() else reject() }
val requireLoggedIn = AccessControlChecker { if(isSessionLoggedIn()) accept() else reject() }

routing {
    accessControl(requireLoggedIn) {
        get("/admin") {
            val context = call.checkAccessControl(requireAdmin)
            if(context.result() is AccessControlCheckerResult.Pass)
                return@get call.respond("Welcome, admin!")
            
            call.respond(HttpStatusCode.Forbidden, "You shall not pass!")
        }
    }
}
```