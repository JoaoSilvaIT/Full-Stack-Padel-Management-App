package pt.isel.ls.api.models

import kotlinx.serialization.Serializable
import pt.isel.ls.data.models.User

@Serializable
class UserLogin(val email: String, val password: String) {
    companion object {
        operator fun invoke(user: User): UserLogin {
            return UserLogin(user.email.value, user.password)
        }
    }
}
