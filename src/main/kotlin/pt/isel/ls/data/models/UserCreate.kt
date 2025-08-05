package pt.isel.ls.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserCreate(
    val name: String,
    val mail: String,
    val password: String,
) {
    init {
        require(name.isNotEmpty()) { "name must not be empty" }
        require(mail.isNotEmpty()) { "email must not be empty" }
        require(password.isNotEmpty()) { "password must not be empty" }
    }
}
