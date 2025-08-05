package pt.isel.ls.services.models

import kotlinx.serialization.Serializable

@Serializable
data class UserInput(
    val name: String,
    val mail: String,
    val password: String,
)
