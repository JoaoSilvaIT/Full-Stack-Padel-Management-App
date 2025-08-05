package pt.isel.ls.api.models

import kotlinx.serialization.Serializable

@Serializable
class CourtDetails(
    val crid: Int,
    val name: String,
    val club: ClubResponse,
    val rentals: List<RentalDraft>,
)
