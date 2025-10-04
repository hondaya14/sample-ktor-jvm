import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen
import model.User
import org.koin.ktor.ext.inject
import user.UserUsecase

fun Application.configureRouting() {
    val usecase: UserUsecase by inject()
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml") {
            codegen = StaticHtmlCodegen()
        }
        get("/healthcheck") {
            call.respondText("OK")
        }
        post("/users") {
            log.info("POST /users")
            val user = call.receive<User>()
            log.info("user: $user")
            usecase.saveUser(user)
            call.respondText("User saved")
        }
        get("/users/{id}") {
            val id: String = call.parameters.getOrFail("id")
            log.info("GET /users/$id")
            val user: User? = usecase.getUser(id)
            if (user == null) {
                call.respondText("User not found", status = NotFound)
            } else {
                call.respond(user)
            }
        }
    }
}
