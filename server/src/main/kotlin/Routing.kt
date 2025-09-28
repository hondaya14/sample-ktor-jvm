import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.getOrFail
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen

fun Application.configureRouting() {
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml") {
            codegen = StaticHtmlCodegen()
        }
        get("/healthcheck") {
            call.respondText("OK")
        }
        post("/cache/{key}") {
            val key = call.parameters.getOrFail("key")
            val body = call.receive<String>()
            application.redisClient.set(key, body)
        }
        get("/cache/{key}") {
            val key = call.parameters.getOrFail("key")
            val value = application.redisClient.get(key)
            call.respondText(value)
        }
    }
}
