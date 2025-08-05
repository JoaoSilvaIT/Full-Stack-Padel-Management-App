package pt.isel.ls.api.models

import kotlinx.serialization.Serializable

@Serializable
data class CourtResponse(
    val name: String,
    val club: Int,
    val crid: Int,
)
