package pt.isel.ls.services

import pt.isel.ls.api.models.CourtDetails
import pt.isel.ls.data.CourtsData
import pt.isel.ls.data.models.Court
import pt.isel.ls.data.models.CourtCreate
import pt.isel.ls.services.models.CourtInput
import pt.isel.ls.utils.PaginatedList

class CourtsServices(private val db: CourtsData) {
    fun getCourtsByClubID(
        cid: Int,
        skip: Int,
        limit: Int,
    ): PaginatedList<Court> {
        return db.getCourtsByClubID(cid, skip, limit)
    }

    fun getCourt(
        cid: Int,
        crid: Int,
    ): Court {
        return db.getCourt(cid, crid)
    }

    fun getCourtDetails(
        cid: Int,
        crid: Int,
    ): CourtDetails {
        return db.getCourtDetails(cid, crid)
    }

    fun createCourt(
        input: CourtInput,
        uid: Int,
    ): Court {
        val courtCreate =
            CourtCreate(
                input.name,
                input.club,
                uid,
            )
        return db.createCourt(courtCreate)
    }
}
