package pt.isel.ls.data.mem

import kotlinx.datetime.LocalDate
import pt.isel.ls.api.models.CourtDraft
import pt.isel.ls.api.models.RentalDetails
import pt.isel.ls.api.models.UserDraft
import pt.isel.ls.data.RentalsData
import pt.isel.ls.data.mem.CourtsDataMem.getCourt
import pt.isel.ls.data.mem.UsersDataMem.getUser
import pt.isel.ls.data.models.Rental
import pt.isel.ls.data.models.RentalCreate
import pt.isel.ls.utils.AppException
import pt.isel.ls.utils.PaginatedList

object RentalsDataMem : RentalsData {
    override fun createRental(rentalCreate: RentalCreate): Rental {
        if (!Mem.users.any { it.uid == rentalCreate.uid }) {
            throw AppException.NotFound("User not found")
        }
        if (!Mem.courts.any { it.club == rentalCreate.club && it.crid == rentalCreate.court }) {
            throw AppException.NotFound("Court not found")
        }

        val newStartTime = rentalCreate.startHour
        val newEndTime = newStartTime + rentalCreate.duration // Hora em que termina (exclusive)

        val hasOverlap =
            Mem.rentals.any { existingRental ->
                if (existingRental.club == rentalCreate.club && existingRental.court == rentalCreate.court &&
                    existingRental.date == rentalCreate.date
                ) {
                    val existingStartTime = existingRental.starthour
                    val existingEndTime = existingStartTime + existingRental.duration

                    (newStartTime < existingEndTime && newEndTime > existingStartTime)
                } else {
                    false
                }
            }

        if (hasOverlap) {
            throw AppException.Conflict("Rental hours not available")
        }

        val nextRid = (Mem.rentals.maxOfOrNull { it.rid } ?: 0) + 1
        val club = Mem.clubs.find { it.cid == rentalCreate.club } ?: throw AppException.NotFound("Club not found")
        val ownerId = club.ownerId
        val rental =
            Rental(
                rid = nextRid,
                club = rentalCreate.club,
                court = rentalCreate.court,
                date = rentalCreate.date,
                starthour = rentalCreate.startHour,
                duration = rentalCreate.duration,
                uid = rentalCreate.uid,
                ownerid = ownerId,
            )
        Mem.rentals.addLast(rental)
        return rental
    }

    override fun getRental(
        cid: Int,
        crid: Int,
        rid: Int,
    ): Rental {
        return Mem.rentals.find { it.rid == rid } ?: throw AppException.NotFound("Rental not found")
    }

    override fun getRentalDetails(
        cid: Int,
        crid: Int,
        rid: Int,
    ): RentalDetails {
        val rental = getRental(cid, crid, rid)
        val renterUser = getUser(rental.uid)
        val rentalCourt = getCourt(cid, crid)

        val renterDraft =
            UserDraft(
                uid = renterUser.uid,
                name = renterUser.name,
            )
        val courtDraft =
            CourtDraft(
                cid = rentalCourt.club,
                crid = rentalCourt.crid,
                name = rentalCourt.name,
            )
        return RentalDetails(
            rid = rental.rid,
            renter = renterDraft,
            court = courtDraft,
            date = rental.date,
            starthour = rental.starthour,
            duration = rental.duration,
        )
    }

    override fun getRentalsByUser(
        uid: Int,
        skip: Int,
        limit: Int,
    ): PaginatedList<Rental> = PaginatedList.fromFullList(Mem.rentals.filter { it.uid == uid }, skip, limit)

    override fun getRentals(
        cid: Int,
        crid: Int,
        date: LocalDate?,
        skip: Int,
        limit: Int,
    ): PaginatedList<Rental> {
        val rentals =
            if (date == null) {
                Mem.rentals.filter { it.club == cid && it.court == crid }
            } else {
                Mem.rentals.filter { it.club == cid && it.court == crid && it.date == date }
            }
        return PaginatedList.fromFullList(rentals, skip, limit)
    }

    override fun getAvailableRentHours(
        cid: Int,
        crid: Int,
        date: LocalDate,
    ): Array<Int> {
        val dayrentals = Mem.rentals.filter { it.date == date && it.club == cid && it.court == crid }
        val availablehours = Array(24) { it }
        for (rental in dayrentals) {
            for (hour in rental.starthour until rental.starthour + rental.duration) {
                availablehours[hour] = -1
            }
        }
        return availablehours.filter { it != -1 }.toTypedArray()
    }

    override fun deleteRental(rid: Int) {
        Mem.rentals.removeIf { it.rid == rid }
    }

    override fun updateRental(
        rid: Int,
        date: LocalDate,
        startHour: Int,
        duration: Int,
    ): Rental {
        val rentalToUpdate = Mem.rentals.find { it.rid == rid } ?: throw AppException.NotFound("Rental not found")
        val updated =
            Rental(
                rentalToUpdate.ownerid,
                rid,
                rentalToUpdate.club,
                rentalToUpdate.court,
                date,
                startHour,
                duration,
                rentalToUpdate.uid,
            )
        Mem.rentals.removeIf { it.rid == rid }
        Mem.rentals.add(updated)
        return updated
    }
}
