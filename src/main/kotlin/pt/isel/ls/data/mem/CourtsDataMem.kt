package pt.isel.ls.data.mem

import pt.isel.ls.api.models.ClubResponse
import pt.isel.ls.api.models.CourtDetails
import pt.isel.ls.api.models.RentalDraft
import pt.isel.ls.data.CourtsData
import pt.isel.ls.data.mem.ClubsDataMem.getClub
import pt.isel.ls.data.models.Court
import pt.isel.ls.data.models.CourtCreate
import pt.isel.ls.utils.AppException
import pt.isel.ls.utils.PaginatedList

object CourtsDataMem : CourtsData {
    override fun createCourt(courtCreate: CourtCreate): Court {
        if (Mem.courts.any { it.name == courtCreate.name }) {
            throw AppException.Conflict("Name already exists")
        }
        if (!Mem.clubs.any { it.ownerId == courtCreate.uid && it.cid == courtCreate.club }) {
            throw AppException.NotFound("Club not found")
        }

        val court =
            Court(
                courtCreate.name,
                courtCreate.club,
                Mem.courts.size + 1,
                courtCreate.uid,
            )

        Mem.courts.addLast(court)
        return court
    }

    override fun getCourtsByClubID(
        cid: Int,
        skip: Int,
        limit: Int,
    ) = PaginatedList.fromFullList(
        Mem.courts.filter {
            it.club == cid
        },
        skip,
        limit,
    )

    override fun getCourt(
        cid: Int,
        crid: Int,
    ): Court {
        return Mem.courts.find { it.crid == crid && it.club == cid }
            ?: throw AppException.NotFound("Court not found")
    }

    override fun getCourtDetails(
        cid: Int,
        crid: Int,
    ): CourtDetails {
        val court = getCourt(cid, crid)
        val parentClub = getClub(cid)

        val courtRentals =
            Mem.rentals
                .filter { it.club == cid && it.court == crid }
                .map { rental -> RentalDraft(rental.rid, rental.uid) } // Transforma para o modelo de rascunho
                .toList()

        val clubResponse =
            ClubResponse(
                cid = parentClub.cid,
                name = parentClub.name,
                ownerId = parentClub.ownerId,
            )

        return CourtDetails(
            crid = court.crid,
            name = court.name,
            club = clubResponse,
            rentals = courtRentals,
        )
    }
}
