package co.hondaya

import config.Config
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.log
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.ConfigLoader
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.config.HoconConfigLoader
import io.ktor.server.config.tryGetStringList
import io.ktor.server.engine.EmbeddedServer
import io.ktor.util.AttributeKey
import io.lettuce.core.RedisURI
import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands
import java.time.Duration

data class RedisConfig(
    val cluster: Cluster,
    val ssl: Boolean,
    val timeoutMs: Long
) {
    data class Cluster(
        val nodes: List<String>
    )

    companion object {
        fun from(config: ApplicationConfig): RedisConfig {
            return RedisConfig(
                cluster = Cluster(
                    nodes = config.config("redis").property("cluster.nodes").getList()
                ),
                ssl = config.property("redis.ssl").getString().toBoolean(),
                timeoutMs = config.property("redis.timeoutMs").getString().toLong()
            )
        }
    }
}

data class RedisResource(
    val client: RedisClusterClient,
    val connection: StatefulRedisClusterConnection<String, String>,
) {
    companion object {
        const val KEY = "RedisResource"
    }
}

private val RedisResourcesKey = AttributeKey<RedisResource>(name = RedisResource.KEY)

fun Application.configureRedisCluster() {
    val redisConf = environment.config.config("redis")
    println(redisConf)
    val clusterConf = redisConf.config("cluster")
    println(clusterConf)
    val nodes = clusterConf.property("nodes").getList()   // ["127.0.0.1:6379", ...]
    val ssl = redisConf.property("ssl").getString()
    println("aaa: $redisConf, $clusterConf, $nodes, $ssl")
    val redisConfig = RedisConfig.from(config = environment.config)
    val uris = redisConfig.cluster.nodes.map { hostPort ->
        val parts = hostPort.split(":", limit = 2)
        val host = parts[0]
        val port = parts.getOrNull(1)?.toIntOrNull() ?: 6379
        val builder = RedisURI.Builder.redis(host, port).apply {
            withTimeout(Duration.ofMillis(redisConfig.timeoutMs))
            withSsl(redisConfig.ssl)
        }
        builder.build()
    }

    val client = RedisClusterClient.create(uris)
    val connection = client.connect()
    log.info("Connected to Redis cluster; ping responded: ${connection.sync().ping()}")

    attributes.put(RedisResourcesKey, RedisResource(client = client, connection = connection))

    // Close on shutdown
//    EmbeddedServer.monitor.subscribe(definition = ApplicationStopped) {
//        runCatching {
//            connection.close()
//            client.shutdown()
//        }
//        log.info("Redis cluster connection closed")
//    }
}

/** Accessor for later use if needed in routes/services. */
val Application.redisClient: RedisAdvancedClusterCommands<String, String>
    get() = attributes[RedisResourcesKey].connection.sync()

