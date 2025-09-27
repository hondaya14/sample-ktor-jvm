package co.hondaya

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.log
import io.ktor.server.config.ApplicationConfig
// Using ApplicationConfig member APIs (config/configOrNull/property/propertyOrNull)
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
            val redisCfg = config.config("redis")
            val clusterCfg = redisCfg.config("cluster")

            val nodes = clusterCfg.property("nodes").getList()
            val ssl = redisCfg.propertyOrNull("ssl")?.getString()?.toBooleanStrictOrNull() ?: false
            val timeoutMs = redisCfg.propertyOrNull("timeoutMs")?.getString()?.toLongOrNull() ?: 2000L

            return RedisConfig(
                cluster = Cluster(nodes = nodes),
                ssl = ssl,
                timeoutMs = timeoutMs,
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
    log.info("redis: try connection...")

    val redisUri: RedisURI = RedisURI.Builder.redis("127.0.0.1", 6381).build();
    val client = RedisClusterClient.create(redisUri)
    val connection = client.connect();

    attributes.put(RedisResourcesKey, RedisResource(client = client, connection = connection))

//    val uris = cfg.cluster.nodes.map { node ->
//        val parts = node.split(":", limit = 2)
//        val host = parts.getOrNull(0)?.trim().orEmpty()
//        val port = parts.getOrNull(1)?.toIntOrNull() ?: 6379
//
//        val builder = RedisURI.Builder.redis(host, port)
//            .withTimeout(Duration.ofMillis(cfg.timeoutMs))
//        if (cfg.ssl) builder.withSsl(true)
//        builder.build()
//    }
//
//    val client = RedisClusterClient.create(uris)
//    val connection = client.connect()
//
//    attributes.put(RedisResourcesKey, RedisResource(client = client, connection = connection))
//
//    log.info("Redis cluster configured with ${uris.size} node(s); ssl=${cfg.ssl}; timeoutMs=${cfg.timeoutMs}")
//
    environment.monitor.subscribe(ApplicationStopped) {
        runCatching {
            val resource = if (attributes.contains(RedisResourcesKey)) attributes[RedisResourcesKey] else null
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
    get() = attributes[RedisResourcesKey].connection.sync()
