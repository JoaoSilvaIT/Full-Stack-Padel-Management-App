package pt.isel.ls.api.models

import kotlinx.serialization.Serializable

@Serializable
class ClubDetails(
    val cid: Int,
    val name: String,
    val owner: UserDraft,
    val courts: List<CourtDraft>,
)
