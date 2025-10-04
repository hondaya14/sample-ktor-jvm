package repository

import RedisConfig
import kotlinx.serialization.json.Json
import model.User

class UserRepositoryImpl(
    private val redisClient: RedisConfig.RedisResources
): UserRepository {

    override fun saveUser(user: User) {
        redisClient.connection.sync().set(user.id, Json.encodeToString(user))
    }

    override fun getUser(id: String): User? {
        return redisClient.connection.sync().get(id)?.let {
            Json.decodeFromString<User>(it)
        }
    }
}
