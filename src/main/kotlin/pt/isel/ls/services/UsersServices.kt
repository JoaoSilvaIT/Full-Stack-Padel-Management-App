package pt.isel.ls.services

import pt.isel.ls.api.models.UserDetails
import pt.isel.ls.api.models.UserLogin
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.models.User
import pt.isel.ls.data.models.UserCreate
import pt.isel.ls.services.models.UserInput
import pt.isel.ls.utils.AppException
import pt.isel.ls.utils.PaginatedList
import pt.isel.ls.utils.Password
import java.util.UUID

class UsersServices(private val db: UsersData) {
    fun getUserDetails(uid: Int): UserDetails {
        return db.getUserDetails(uid)
    }

    fun createUser(input: UserInput): User {
        val userCreate = UserCreate(input.name, input.mail, input.password)
        return db.createUser(userCreate)
    }

    fun getIdByToken(token: UUID): Int {
        return db.getIdByToken(token)
    }

    fun getUsers(
        skip: Int,
        limit: Int,
    ): PaginatedList<User> {
        return db.getUsers(skip, limit)
    }

    fun userLogin(userLogin: UserLogin): User {
        val user = db.getUserByEmail(userLogin.email)
        if (!Password(userLogin.password).verify(user.password)) {
            throw AppException.InvalidData("The given password is incorrect")
        }
        return user
    }
}
