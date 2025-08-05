package pt.isel.ls.data.models

import kotlinx.datetime.LocalDate

data class Rental(
    val ownerid: Int,
    val rid: Int,
    val club: Int,
    val court: Int,
    val date: LocalDate,
    val starthour: Int,
    val duration: Int,
    val uid: Int,
)
