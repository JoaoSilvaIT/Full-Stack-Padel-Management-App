package pt.isel.ls.api.models

import kotlinx.serialization.Serializable

@Serializable
class CourtDraft(
    val cid: Int,
    val crid: Int,
    val name: String,
)
