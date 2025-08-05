package pt.isel.ls.api

import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.RequestLens
import pt.isel.ls.api.models.RentalResponse
import pt.isel.ls.api.models.RentalUpdate
import pt.isel.ls.services.Services
import pt.isel.ls.services.models.RentalInput
import pt.isel.ls.utils.AppException
import pt.isel.ls.utils.paramToInt

class RentalsAPI(
    private val services: Services,
    private val userIdKey: RequestLens<Int>,
) {
    fun createRental(req: Request): Response {
        val input = Json.decodeFromString<RentalInput>(req.bodyString())
        val rental = services.rentals.createRental(input, userIdKey(req))
        val created =
            RentalResponse(
                rental.rid,
                rental.club,
                rental.court,
                rental.date,
                rental.starthour,
                rental.duration,
                rental.uid,
            )
        return Response(Status.CREATED).body(Json.encodeToString(created))
    }

    fun getRentalDetails(req: Request): Response {
        val cid = req.paramToInt("cid")
        val crid = req.paramToInt("crid")
        val rid = req.paramToInt("rid")
        val details = services.rentals.getRentalDetails(cid, crid, rid)
        return Response(Status.OK).body(Json.encodeToString(details))
    }

    fun getRentals(req: Request): Response {
        val skip = req.query("skip")?.toInt() ?: 0
        val limit = req.query("limit")?.toInt() ?: 30
        val date = req.query("date")
        val rentals =
            services.rentals.getRentals(req.paramToInt("cid"), req.paramToInt("crid"), date, skip, limit).map {
                RentalResponse(it.rid, it.club, it.court, it.date, it.starthour, it.duration, it.uid)
            }
        return Response(Status.OK).body(Json.encodeToString(rentals))
    }

    fun getRentalsByUser(req: Request): Response {
        val skip = req.query("skip")?.toInt() ?: 0
        val limit = req.query("limit")?.toInt() ?: 30
        val rentals =
            services.rentals.getRentalsByUser(req.paramToInt("uid"), skip, limit).map {
                RentalResponse(it.rid, it.club, it.court, it.date, it.starthour, it.duration, it.uid)
            }
        return Response(Status.OK).body(Json.encodeToString(rentals))
    }

    fun getAvailableRentHours(req: Request): Response {
        val date =
            req.query("date")?.let {
                try {
                    println(it)
                    LocalDate.parse(it)
                } catch (_: Exception) {
                    throw AppException.InvalidData()
                }
            } ?: throw AppException.InvalidData("date")
        val hours = services.rentals.getAvailableRentHours(req.paramToInt("cid"), req.paramToInt("crid"), date)
        return Response(Status.OK).body(Json.encodeToString(hours))
    }

    fun deleteRental(req: Request): Response {
        services.rentals.deleteRental(req.paramToInt("rid"))
        return Response(Status.OK).body(Json.encodeToString("Rental Deleted"))
    }

    fun updateRental(req: Request): Response {
        val input = Json.decodeFromString<RentalUpdate>(req.bodyString())
        val rental = services.rentals.updateRental(req.paramToInt("rid"), input.date, input.startHour, input.duration)
        return Response(Status.OK).body(
            Json.encodeToString(
                RentalResponse(
                    rental.rid,
                    rental.club,
                    rental.court,
                    rental.date,
                    rental.starthour,
                    rental.duration,
                    rental.uid,
                ),
            ),
        )
    }
}
