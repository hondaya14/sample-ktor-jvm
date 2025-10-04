import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import repository.repositoryModule

fun Application.module() {
    configureRouting()
    install(CallLogging)
    install(ContentNegotiation) { json() }
    install(Koin) {
        slf4jLogger()
        modules(
            redisModule,
            usecaseModule,
            repositoryModule
        )
    }
}
