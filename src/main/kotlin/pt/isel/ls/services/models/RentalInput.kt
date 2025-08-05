package pt.isel.ls.services.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class RentalInput(
    val club: Int,
    val court: Int,
    val date: LocalDate,
    val startHour: Int,
    val duration: Int,
)
