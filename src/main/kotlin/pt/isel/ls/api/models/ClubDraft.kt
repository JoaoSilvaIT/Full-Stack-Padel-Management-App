package pt.isel.ls.api.models

import kotlinx.serialization.Serializable

@Serializable
class ClubDraft(
    val cid: Int,
    val name: String,
)
