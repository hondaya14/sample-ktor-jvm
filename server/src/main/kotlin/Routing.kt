package co.hondaya

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen
import kotlinx.coroutines.withContext

fun Application.configureRouting() {
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml"){
            codegen = StaticHtmlCodegen()
        }
        get("/") {
            call.respondText("Hello World!")
        }
        post("/cache/{key}") {
            val key = call.parameters.get("key")
            val body = call.receive<String>()
            application.redisClient.set(key, body)
        }
        get("/cache/{key}") {
            val key = call.parameters.get("key")
            application.redisClient.get(key)
        }
    }
}
