package pt.isel.ls.api.models

import kotlinx.serialization.Serializable

@Serializable
data class ClubResponse(
    val cid: Int,
    val name: String,
    val ownerId: Int,
)
