package co.hondaya

import io.ktor.server.application.*

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureRedisCluster()
    configureHTTP()
    configureRouting()
}
