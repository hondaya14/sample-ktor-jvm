import io.lettuce.core.RedisURI
import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection
import org.koin.dsl.module

val redisModule = module {
    single{ RedisConfig.initialize() }
}

class RedisConfig {
    data class RedisResources(
        val client: RedisClusterClient,
        val connection: StatefulRedisClusterConnection<String, String>
    ): AutoCloseable {
        override fun close() {
            connection.close()
            client.shutdown()
        }
    }

    companion object {
        const val REDIS_CLIENT = "redis"
        private const val HOST = "127.0.0.1"
        private const val PORT = 6381

        fun initialize(): RedisResources {
            val redisUri: RedisURI = RedisURI.Builder.redis(HOST, PORT).build()
            val client = RedisClusterClient.create(redisUri)
            val connection = client.connect()
            return RedisResources(client, connection)
        }
    }
}
