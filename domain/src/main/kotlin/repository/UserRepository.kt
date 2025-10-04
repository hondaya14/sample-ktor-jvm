package repository

import model.User

interface UserRepository {
    fun saveUser(user: User)
    fun getUser(id: String): User?
}