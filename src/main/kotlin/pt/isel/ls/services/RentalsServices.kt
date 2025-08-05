package pt.isel.ls.services

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import pt.isel.ls.api.models.RentalDetails
import pt.isel.ls.data.RentalsData
import pt.isel.ls.data.models.Rental
import pt.isel.ls.data.models.RentalCreate
import pt.isel.ls.services.models.RentalInput
import pt.isel.ls.utils.AppException
import pt.isel.ls.utils.PaginatedList
import pt.isel.ls.utils.isPast

class RentalsServices(private val db: RentalsData) {
    fun createRental(
        input: RentalInput,
        uid: Int,
    ): Rental {
        val startDateTime =
            LocalDateTime(input.date.year, input.date.month, input.date.dayOfMonth, input.startHour, 0, 0, 0)
        if (startDateTime.toJavaLocalDateTime() < java.time.LocalDateTime.now()) {
            throw AppException.InvalidData("Date must be in the future")
        }
        val availableHours = db.getAvailableRentHours(input.club, input.court, input.date)
        for (hour in input.startHour..<input.startHour + input.duration) {
            if (hour !in availableHours) throw AppException.InvalidData("Hour not available for rental")
        }

        val rentalCreate =
            RentalCreate(
                input.club,
                input.court,
                input.date,
                input.startHour,
                input.duration,
                uid,
            )
        return db.createRental(rentalCreate)
    }

    fun getRental(
        cid: Int,
        crid: Int,
        rid: Int,
    ): Rental {
        return db.getRental(cid, crid, rid)
    }

    fun getRentalDetails(
        cid: Int,
        crid: Int,
        rid: Int,
    ): RentalDetails {
        return db.getRentalDetails(cid, crid, rid)
    }

    fun getRentals(
        cid: Int,
        crid: Int,
        date: String? = null,
        skip: Int,
        limit: Int,
    ): PaginatedList<Rental> {
        val date = if (date != null) LocalDate.parse(date) else null
        val rentals = db.getRentals(cid, crid, date, skip, limit)
        return rentals
    }

    fun getRentalsByUser(
        uid: Int,
        skip: Int,
        limit: Int,
    ): PaginatedList<Rental> {
        return db.getRentalsByUser(uid, skip, limit)
    }

    fun getAvailableRentHours(
        cid: Int,
        crid: Int,
        date: LocalDate,
    ): Array<Int> {
        return db.getAvailableRentHours(cid, crid, date)
    }

    fun deleteRental(rid: Int) {
        db.deleteRental(rid)
    }

    fun updateRental(
        rid: Int,
        date: LocalDate,
        startHour: Int,
        duration: Int,
    ): Rental {
        if (date.isPast()) throw AppException.InvalidData("Date must be in the future")
        if (duration <= 0) throw AppException.InvalidData("Duration must be greater than 0")
        if (startHour !in 0..23) throw AppException.InvalidData("Start hour must be between 0 and 23")
        return db.updateRental(rid, date, startHour, duration)
    }
}
