package pt.isel.ls.data.models

import kotlinx.datetime.LocalDate
import pt.isel.ls.utils.isPast

data class RentalCreate(
    val club: Int,
    val court: Int,
    val date: LocalDate,
    val startHour: Int,
    val duration: Int,
    val uid: Int,
) {
    init {
        require(club >= 1) { "club must be greater than 0" }
        require(court >= 1) { "court must be greater than 0" }
        require(!date.isPast()) { "start date must be in the future" }
        require(startHour > 0) { "starthour must be greater than 0" }
        require(duration > 0) { "duration must be greater than 0" }
        require(uid >= 0) { "uid must be greater than 0" }
    }
}
