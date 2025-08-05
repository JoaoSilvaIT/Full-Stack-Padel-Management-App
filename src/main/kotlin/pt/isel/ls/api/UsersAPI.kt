package pt.isel.ls.api

import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import pt.isel.ls.api.models.UserLogin
import pt.isel.ls.api.models.UserLoginResponse
import pt.isel.ls.api.models.UserResponse
import pt.isel.ls.data.models.Email
import pt.isel.ls.services.Services
import pt.isel.ls.services.models.UserInput
import pt.isel.ls.utils.paramToInt

class UsersAPI(
    private val services: Services,
) {
    fun createUser(req: Request): Response {
        val input = Json.decodeFromString<UserInput>(req.bodyString())
        val user = services.users.createUser(input)
        val created = UserResponse(user.token, user.name, Email(user.mail), user.uid)
        return Response(Status.CREATED).body(Json.encodeToString(created))
    }

    fun getUser(req: Request): Response {
        val id = req.paramToInt("uid")
        val details = services.users.getUserDetails(id)
        return Response(Status.OK).body(Json.encodeToString(details))
    }

    fun getUsers(req: Request): Response {
        val skip = req.query("skip")?.toInt() ?: 0
        val limit = req.query("limit")?.toInt() ?: 30
        val users = services.users.getUsers(skip, limit).map { UserResponse(it.token, it.name, it.email, it.uid) }
        return Response(Status.OK).body(Json.encodeToString(users))
    }

    fun loginUser(request: Request): Response {
        val input = Json.decodeFromString<UserLogin>(request.bodyString())
        val user = services.users.userLogin(input)
        return Response(Status.OK).body(Json.encodeToString(UserLoginResponse(user.uid, user.token)))
    }
}
