package co.hondaya

import configureRedisCluster
import io.ktor.server.application.*

fun Application.module() {
    configureRedisCluster()
    configureRouting()
}
