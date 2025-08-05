package pt.isel.ls.api

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.RequestKey
import org.http4k.lens.bearerToken
import org.postgresql.util.PSQLException
import pt.isel.ls.services.Services
import pt.isel.ls.utils.AppException
import java.time.format.DateTimeParseException
import java.util.UUID

class Middleware(
    val services: Services,
) {
    val userIdKey = RequestKey.required<Int>("X-userid")

    fun userTokenConverter(
        request: Request,
        next: HttpHandler,
    ): Response {
        val token = request.bearerToken() ?: throw AppException.Unauthorized("User token not found")
        println(token)
        val uuid = UUID.fromString(token)
        val id =
            try {
                services.users.getIdByToken(uuid)
            } catch (e: AppException.NotFound) {
                throw AppException.Unauthorized("Invalid or expired token")
            }
        return next(request.with(userIdKey of id))
    }

    fun exceptionHandler(
        request: Request,
        next: HttpHandler,
    ): Response =
        try {
            next(request)
        } catch (e: Exception) {
            e.printStackTrace()
            when (e) {
                is DateTimeParseException -> Response(Status.BAD_REQUEST).body(e.message ?: "Bad Request")
                is PSQLException -> Response(Status.BAD_REQUEST).body(e.message ?: "Bad Request")
                is AppException.NotFound -> Response(Status.NOT_FOUND).body(e.message ?: "Not Found")
                is AppException.InvalidData -> Response(Status.BAD_REQUEST).body(e.message ?: "Bad Request")
                is AppException.Conflict -> Response(Status.CONFLICT).body(e.message ?: "Conflict")
                is AppException.Unauthorized -> Response(Status.UNAUTHORIZED).body(e.message ?: "Unauthorized")
                else -> Response(Status.INTERNAL_SERVER_ERROR).body("Unknown error, contact administrator")
            }
        }
}
