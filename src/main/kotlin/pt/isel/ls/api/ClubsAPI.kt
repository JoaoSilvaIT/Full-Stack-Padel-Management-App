package pt.isel.ls.api

import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.RequestLens
import org.http4k.routing.path
import pt.isel.ls.api.models.ClubResponse
import pt.isel.ls.services.Services
import pt.isel.ls.services.models.ClubInput
import pt.isel.ls.utils.AppException

class ClubsAPI(
    private val services: Services,
    private val userIdKey: RequestLens<Int>,
) {
    fun createClub(req: Request): Response {
        val input = Json.decodeFromString<ClubInput>(req.bodyString())
        val uid = userIdKey(req)
        val club = services.clubs.createClub(input, uid)
        val created = ClubResponse(club.cid, club.name, club.ownerId)
        return Response(Status.CREATED).body(Json.encodeToString(created))
    }

    fun getClubDetails(req: Request): Response {
        val cid = req.path("cid") ?: throw AppException.NotFound("Club ID not found")
        val details = services.clubs.getClubDetails(cid.toInt())
        return Response(Status.OK).body(Json.encodeToString(details))
    }

    fun updateClubById(req: Request): Response {
        val input = Json.decodeFromString<ClubInput>(req.bodyString())
        val cid = req.path("cid")?.toInt() ?: throw AppException.NotFound("Club ID not found")
        val club = services.clubs.updateClub(input, cid)
        val updated = ClubResponse(club.cid, club.name, club.ownerId)
        return Response(Status.CREATED).body(Json.encodeToString(updated))
    }

    fun getClubByName(req: Request): Response {
        val club = services.clubs.getClubDetailsByName(req.path("name") ?: throw AppException.NotFound("Club name not found"))
        return Response(Status.OK).body(Json.encodeToString(ClubResponse(club.cid, club.name, club.ownerId)))
    }

    fun getClubs(req: Request): Response {
        val skip = req.query("skip")?.toInt() ?: 0
        val limit = req.query("limit")?.toInt() ?: 30
        val partial = req.query("name") ?: ""
        val clubs = services.clubs.getClubs(skip, limit, partial).map { ClubResponse(it.cid, it.name, it.ownerId) }
        return Response(Status.OK).body(Json.encodeToString(clubs))
    }
}
