package pt.isel.ls.api.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
class RentalUpdate(val date: LocalDate, val startHour: Int, val duration: Int)
