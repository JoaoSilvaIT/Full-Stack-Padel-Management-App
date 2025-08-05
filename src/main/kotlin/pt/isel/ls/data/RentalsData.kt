package pt.isel.ls.data

import kotlinx.datetime.LocalDate
import pt.isel.ls.api.models.RentalDetails
import pt.isel.ls.data.models.Rental
import pt.isel.ls.data.models.RentalCreate
import pt.isel.ls.utils.PaginatedList

interface RentalsData {
    fun createRental(rentalCreate: RentalCreate): Rental

    fun getRental(
        cid: Int,
        crid: Int,
        rid: Int,
    ): Rental

    fun getRentalDetails(
        cid: Int,
        crid: Int,
        rid: Int,
    ): RentalDetails

    fun getRentals(
        cid: Int,
        crid: Int,
        date: LocalDate?,
        skip: Int,
        limit: Int,
    ): PaginatedList<Rental>

    fun getRentalsByUser(
        uid: Int,
        skip: Int,
        limit: Int,
    ): PaginatedList<Rental>

    fun getAvailableRentHours(
        cid: Int,
        crid: Int,
        date: LocalDate,
    ): Array<Int>

    fun deleteRental(rid: Int)

    fun updateRental(
        rid: Int,
        date: LocalDate,
        startHour: Int,
        duration: Int,
    ): Rental
}
