package pt.isel.ls.data

import pt.isel.ls.api.models.ClubDetails
import pt.isel.ls.data.models.Club
import pt.isel.ls.data.models.ClubCreate
import pt.isel.ls.data.models.ClubUpdate
import pt.isel.ls.utils.PaginatedList

interface ClubsData {
    fun createClub(clubCreate: ClubCreate): Club

    fun updateClub(
        cid: Int,
        clubUpdate: ClubUpdate,
    ): Club

    fun getClub(cid: Int): Club

    fun getClubDetails(cid: Int): ClubDetails

    fun getClubDetailsByName(name: String): Club

    fun getClubs(
        skip: Int,
        limit: Int,
        partialName: String = "",
    ): PaginatedList<Club>
}
