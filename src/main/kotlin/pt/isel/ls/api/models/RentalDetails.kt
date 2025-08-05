package pt.isel.ls.api.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
class RentalDetails(
    val rid: Int,
    val renter: UserDraft,
    val court: CourtDraft,
    val date: LocalDate,
    val starthour: Int,
    val duration: Int,
)
