package user

import model.User
import repository.UserRepository

class UserUsecase(
     private val userRepository: UserRepository
) {
    fun getUser(id: String) = userRepository.getUser(id)
    fun saveUser(user: User) = userRepository.saveUser(user)
}