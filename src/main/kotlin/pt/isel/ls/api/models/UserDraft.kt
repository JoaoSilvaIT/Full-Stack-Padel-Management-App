package pt.isel.ls.api.models

import kotlinx.serialization.Serializable

@Serializable
class UserDraft(
    val uid: Int,
    val name: String,
)
