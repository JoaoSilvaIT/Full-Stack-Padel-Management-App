package pt.isel.ls.data.mem

import pt.isel.ls.api.models.ClubDraft
import pt.isel.ls.api.models.RentalDraft
import pt.isel.ls.api.models.UserDetails
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.models.Email
import pt.isel.ls.data.models.User
import pt.isel.ls.data.models.UserCreate
import pt.isel.ls.utils.AppException
import pt.isel.ls.utils.PaginatedList
import pt.isel.ls.utils.Password
import java.util.UUID

object UsersDataMem : UsersData {
    override fun createUser(userCreate: UserCreate): User {
        if (Mem.users.any { it.email.value == userCreate.mail }) {
            throw AppException.InvalidData()
        }
        val user =
            User(
                token = UUID.randomUUID(),
                name = userCreate.name,
                email = Email(userCreate.mail),
                uid = Mem.users.size + 1,
                password = Password(userCreate.password).hash(),
            )
        Mem.users.addLast(user)
        return user
    }

    override fun getUser(uid: Int): User {
        return Mem.users.find { it.uid == uid } ?: throw AppException.NotFound("User not found")
    }

    override fun getUserDetails(uid: Int): UserDetails {
        val user = getUser(uid)

        val userClubs =
            Mem.clubs
                .filter { it.ownerId == uid }
                .map { club -> ClubDraft(club.cid, club.name) }
                .toList()

        val userRentals =
            Mem.rentals
                .filter { it.uid == uid }
                .map { rental -> RentalDraft(rental.rid, rental.uid) }
                .toList()

        return UserDetails(
            uid = user.uid,
            name = user.name,
            clubs = userClubs,
            rentals = userRentals,
        )
    }

    override fun getIdByToken(token: UUID): Int =
        Mem.users.find { it.token == token }?.uid
            ?: throw AppException.NotFound("User token not found")

    override fun getUsers(
        skip: Int,
        limit: Int,
    ): PaginatedList<User> = PaginatedList.fromFullList(Mem.users, skip, limit)

    override fun getUserByEmail(email: String): User {
        return Mem.users.find { it.email.value == email } ?: throw AppException.NotFound("User not found")
    }
}
