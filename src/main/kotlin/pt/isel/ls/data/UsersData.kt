package pt.isel.ls.data

import pt.isel.ls.api.models.UserDetails
import pt.isel.ls.data.models.User
import pt.isel.ls.data.models.UserCreate
import pt.isel.ls.utils.PaginatedList
import java.util.UUID

interface UsersData {
    fun createUser(userCreate: UserCreate): User

    fun getUser(uid: Int): User

    fun getUserDetails(uid: Int): UserDetails

    fun getIdByToken(token: UUID): Int

    fun getUsers(
        skip: Int,
        limit: Int,
    ): PaginatedList<User>

    fun getUserByEmail(email: String): User
}
