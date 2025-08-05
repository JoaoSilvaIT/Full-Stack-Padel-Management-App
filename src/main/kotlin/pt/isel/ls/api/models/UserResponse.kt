package pt.isel.ls.api.models

import kotlinx.serialization.Serializable
import pt.isel.ls.data.models.Email
import pt.isel.ls.utils.UUIDSerializer
import java.util.UUID

@Serializable
data class UserResponse(
    @Serializable(with = UUIDSerializer::class)
    val token: UUID,
    val name: String,
    val email: Email,
    val uid: Int,
)
