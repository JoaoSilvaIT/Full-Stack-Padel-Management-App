package pt.isel.ls.data.mem

import pt.isel.ls.api.models.ClubDetails
import pt.isel.ls.api.models.CourtDraft
import pt.isel.ls.api.models.UserDraft
import pt.isel.ls.data.ClubsData
import pt.isel.ls.data.models.Club
import pt.isel.ls.data.models.ClubCreate
import pt.isel.ls.data.models.ClubUpdate
import pt.isel.ls.utils.AppException
import pt.isel.ls.utils.PaginatedList

object ClubsDataMem : ClubsData {
    override fun createClub(clubCreate: ClubCreate): Club {
        if (Mem.clubs.any { it.name == clubCreate.name }) {
            throw AppException.Conflict("Name already exists")
        }
        if (!Mem.users.any { it.uid == clubCreate.ownerId }) {
            throw AppException.NotFound("User not found")
        }

        val club =
            Club(
                name = clubCreate.name,
                ownerId = clubCreate.ownerId,
                cid = Mem.clubs.size + 1,
            )
        Mem.clubs.addLast(club)
        return club
    }

    override fun updateClub(
        cid: Int,
        clubUpdate: ClubUpdate,
    ): Club {
        TODO("Not yet implemented")
    }

    override fun getClubs(
        skip: Int,
        limit: Int,
        partialName: String,
    ): PaginatedList<Club> = PaginatedList.fromFullList(Mem.clubs.filter { it.name.contains(partialName, true) }, skip, limit)

    override fun getClub(cid: Int): Club {
        return Mem.clubs.find { it.cid == cid }
            ?: throw AppException.NotFound("Club Not Found")
    }

    override fun getClubDetails(cid: Int): ClubDetails {
        val club = getClub(cid) // Se não encontrar, lança NotFound.
        val owner =
            Mem.users.find { it.uid == club.ownerId }
                ?: throw AppException.InvalidData("Owner user for the club not found.")

        val courts =
            Mem.courts
                .filter { it.club == cid }
                .map { court -> CourtDraft(court.club, court.crid, court.name) } // Transforma para o modelo de rascunho
                .toList()

        val ownerDraft =
            UserDraft(
                uid = owner.uid,
                name = owner.name,
            )

        return ClubDetails(
            cid = club.cid,
            name = club.name,
            owner = ownerDraft,
            courts = courts,
        )
    }

    override fun getClubDetailsByName(name: String): Club {
        return Mem.clubs.find { it.name == name } ?: throw AppException.NotFound("Club not found")
    }
}
