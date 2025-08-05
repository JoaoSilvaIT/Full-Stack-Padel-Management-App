package pt.isel.ls.api

import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.RequestLens
import org.http4k.routing.path
import pt.isel.ls.api.models.CourtResponse
import pt.isel.ls.services.Services
import pt.isel.ls.services.models.CourtInput
import pt.isel.ls.utils.AppException

class CourtsAPI(
    private val services: Services,
    private val userIdKey: RequestLens<Int>,
) {
    fun createCourt(req: Request): Response {
        val input = Json.decodeFromString<CourtInput>(req.bodyString())
        val court = services.courts.createCourt(input, userIdKey(req))
        val created = CourtResponse(court.name, court.club, court.crid)
        return Response(Status.CREATED).body(Json.encodeToString(created))
    }

    fun getCourtsByClubID(req: Request): Response {
        val skip = req.query("skip")?.toInt() ?: 0
        val limit = req.query("limit")?.toInt() ?: 30
        val club = req.path("cid") ?: throw AppException.NotFound("Club ID not found")
        val courtList =
            services.courts.getCourtsByClubID(club.toInt(), skip, limit).map {
                CourtResponse(it.name, it.club, it.crid)
            }
        return Response(Status.OK).body(Json.encodeToString(courtList))
    }

    fun getCourtDetails(req: Request): Response {
        val cid = req.path("cid") ?: throw AppException.NotFound("Club ID not found")
        val crid = req.path("crid") ?: throw AppException.NotFound("Court ID not found")
        val details = services.courts.getCourtDetails(cid.toInt(), crid.toInt())
        return Response(Status.OK).body(Json.encodeToString(details))
    }
}
