package pt.isel.ls.api.models

import kotlinx.serialization.Serializable
import pt.isel.ls.utils.UUIDSerializer
import java.util.UUID

@Serializable
class UserLoginResponse(
    val uid: Int,
    @Serializable(with = UUIDSerializer::class)
    val token: UUID,
)
