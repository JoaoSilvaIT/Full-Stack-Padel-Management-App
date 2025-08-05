package pt.isel.ls.api.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class RentalResponse(
    val rid: Int,
    val club: Int,
    val court: Int,
    val date: LocalDate,
    val starthour: Int,
    val duration: Int,
    val uid: Int,
)
