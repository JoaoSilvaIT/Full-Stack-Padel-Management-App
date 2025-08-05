package pt.isel.ls.services.models

import kotlinx.serialization.Serializable

@Serializable
data class CourtInput(
    val name: String,
    val club: Int,
)
