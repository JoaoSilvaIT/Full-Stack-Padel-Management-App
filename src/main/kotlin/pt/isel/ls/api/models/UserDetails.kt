package pt.isel.ls.api.models

import kotlinx.serialization.Serializable

@Serializable
class UserDetails(
    val uid: Int,
    val name: String,
    val clubs: List<ClubDraft>,
    val rentals: List<RentalDraft>,
)
