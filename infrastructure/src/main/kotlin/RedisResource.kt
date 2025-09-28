import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection

data class RedisResource(
    val client: RedisClusterClient,
    val connection: StatefulRedisClusterConnection<String, String>,
) {
    companion object {
        const val KEY = "RedisResource"
    }
}
