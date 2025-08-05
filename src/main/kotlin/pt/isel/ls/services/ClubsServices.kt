package pt.isel.ls.services

import pt.isel.ls.api.models.ClubDetails
import pt.isel.ls.data.ClubsData
import pt.isel.ls.data.models.Club
import pt.isel.ls.data.models.ClubCreate
import pt.isel.ls.data.models.ClubUpdate
import pt.isel.ls.services.models.ClubInput
import pt.isel.ls.utils.PaginatedList

class ClubsServices(private val db: ClubsData) {
    fun getClubDetails(cid: Int): ClubDetails {
        return db.getClubDetails(cid)
    }

    fun getClub(cid: Int): Club {
        return db.getClub(cid)
    }

    fun getClubDetailsByName(name: String): Club {
        return db.getClubDetailsByName(name)
    }

    fun createClub(
        input: ClubInput,
        uid: Int,
    ): Club {
        val clubCreate =
            ClubCreate(
                input.name,
                uid,
            )
        return db.createClub(clubCreate)
    }

    fun updateClub(
        input: ClubInput,
        cid: Int,
    ): Club {
        val clubUpdate =
            ClubUpdate(
                input.name,
            )
        return db.updateClub(cid, clubUpdate)
    }

    fun getClubs(
        skip: Int,
        limit: Int,
        partialname: String = "",
    ): PaginatedList<Club> {
        return db.getClubs(skip, limit, partialname)
    }
}
