import RedisConfig.HOST
import RedisConfig.PORT
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.log
import io.ktor.util.AttributeKey
import io.lettuce.core.RedisURI
import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands

object RedisConfig {
    const val HOST = "127.0.0.1"
    const val PORT = 6381
}

fun Application.configureRedisCluster() {
    log.info("redis: try connection...")

    val redisUri: RedisURI = RedisURI.Builder.redis(HOST, PORT).build()
    val client = RedisClusterClient.create(redisUri)
    val connection = client.connect()

    val redisResourcesKey = AttributeKey<RedisResource>(name = RedisResource.KEY)
    attributes.put(redisResourcesKey, RedisResource(client = client, connection = connection))

    environment.monitor.subscribe(ApplicationStopped) {
        runCatching {
            val resource = if (attributes.contains(redisResourcesKey)) attributes[redisResourcesKey] else null
            resource?.let {
                it.connection.close()
                it.client.shutdown()
            }
        }.onFailure {
            log.warn("Error closing Redis resources: ${it.message}", it)
        }
    }
}

/** Accessor for later use if needed in routes/services. */
val Application.redisClient: RedisAdvancedClusterCommands<String, String>
    get() = attributes[AttributeKey<RedisResource>(name = RedisResource.KEY)].connection.sync()
