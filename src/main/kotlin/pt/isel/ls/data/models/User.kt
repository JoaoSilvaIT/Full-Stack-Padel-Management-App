package pt.isel.ls.data.models

import java.util.UUID

data class User(
    val token: UUID,
    val name: String,
    val email: Email,
    val uid: Int,
    val password: String,
) {
    val mail: String
        get() = email.value
}
