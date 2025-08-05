package pt.isel.ls.data

import pt.isel.ls.api.models.CourtDetails
import pt.isel.ls.data.models.Court
import pt.isel.ls.data.models.CourtCreate
import pt.isel.ls.utils.PaginatedList

interface CourtsData {
    fun createCourt(courtCreate: CourtCreate): Court

    fun getCourt(
        cid: Int,
        crid: Int,
    ): Court

    fun getCourtDetails(
        cid: Int,
        crid: Int,
    ): CourtDetails

    fun getCourtsByClubID(
        cid: Int,
        skip: Int,
        limit: Int,
    ): PaginatedList<Court>
}
