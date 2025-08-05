package pt.isel.ls.http

import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import pt.isel.ls.api.models.CourtDraft
import pt.isel.ls.api.models.RentalDetails
import pt.isel.ls.api.models.RentalResponse
import pt.isel.ls.api.models.RentalUpdate
import pt.isel.ls.api.models.UserDraft
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RentalTest {
    private fun getRentalDetailsHandler(request: Request): Response {
        val rid = request.path("rid")?.toIntOrNull()
        return if (rid == 101) {
            val mockRentalDetails =
                RentalDetails(
                    rid = 101,
                    renter = UserDraft(uid = 1, name = "Mock User One"),
                    court = CourtDraft(name = "Mock Court Alpha", cid = 131, crid = 11),
                    date = LocalDate(2025, 10, 20),
                    starthour = 14,
                    duration = 2,
                )
            Response(Status.OK).body(Json.encodeToString(mockRentalDetails))
        } else {
            Response(Status.NOT_FOUND).body("Rental not found")
        }
    }

    private fun postRentalHandler(request: Request): Response {
        val mockRentalResponse =
            RentalResponse(
                rid = 102,
                club = 1,
                court = 11,
                date = LocalDate(2025, 10, 21),
                starthour = 16,
                duration = 1,
                uid = 2,
            )
        return Response(Status.CREATED).body(Json.encodeToString(mockRentalResponse))
    }

    private fun getCourtRentalsHandler(request: Request): Response {
        val cid = request.path("cid")?.toIntOrNull()
        val crid = request.path("crid")?.toIntOrNull()

        if (cid == 1 && crid == 11) {
            val mockRentalsList =
                listOf(
                    RentalResponse(101, 1, 11, LocalDate(2025, 10, 20), 14, 2, 1),
                    RentalResponse(103, 1, 11, LocalDate(2025, 10, 20), 17, 1, 2),
                )
            return Response(Status.OK).body(Json.encodeToString(mockRentalsList))
        } else {
            return Response(Status.OK).body(Json.encodeToString(emptyList<RentalResponse>()))
        }
    }

    private fun getUserRentalsHandler(request: Request): Response {
        val uid = request.path("uid")?.toIntOrNull()

        if (uid == 1) {
            val mockRentalsList =
                listOf(
                    RentalResponse(101, 1, 11, LocalDate(2025, 10, 20), 14, 2, 1),
                    RentalResponse(104, 2, 22, LocalDate(2025, 11, 1), 9, 1, 1),
                )
            return Response(Status.OK).body(Json.encodeToString(mockRentalsList))
        } else {
            return Response(Status.OK).body(Json.encodeToString(emptyList<RentalResponse>()))
        }
    }

    private fun deleteRentalHandler(request: Request): Response {
        val rid = request.path("rid")?.toIntOrNull()

        return if (rid == 101) {
            Response(Status.OK).body(Json.encodeToString("Rental Deleted"))
        } else {
            Response(Status.NOT_FOUND).body("Rental not found")
        }
    }

    private fun updateRentalHandler(request: Request): Response {
        val rid = request.path("rid")?.toIntOrNull()

        return if (rid == 101) {
            val updatedRental =
                RentalResponse(
                    rid = 101,
                    club = 1,
                    court = 11,
                    date = LocalDate(2025, 11, 25),
                    starthour = 16,
                    duration = 3,
                    uid = 1,
                )
            Response(Status.OK).body(Json.encodeToString(updatedRental))
        } else {
            Response(Status.NOT_FOUND).body("Rental not found")
        }
    }

    @Test
    fun `GET rental details with existing ID returns OK`() {
        val app =
            routes(
                "/rentals/{rid}" bind GET to ::getRentalDetailsHandler,
            )
        val expectedRentalId = 101

        val response = app(Request(GET, "/rentals/$expectedRentalId"))

        assertEquals(Status.OK, response.status)
        val rentalDetails = Json.decodeFromString<RentalDetails>(response.bodyString())
        assertEquals(expectedRentalId, rentalDetails.rid)
        assertEquals(1, rentalDetails.renter.uid)
        assertEquals(11, rentalDetails.court.crid)
        assertEquals(LocalDate(2025, 10, 20), rentalDetails.date)
    }

    @Test
    fun `GET rental details with non-existing ID returns NOT_FOUND`() {
        val app =
            routes(
                "/rentals/{rid}" bind GET to ::getRentalDetailsHandler,
            )
        val nonExistentRentalId = 999

        val response = app(Request(GET, "/rentals/$nonExistentRentalId"))

        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("Rental not found", response.bodyString())
    }

    @Test
    fun `POST rental returns CREATED`() {
        val app =
            routes(
                "/rentals" bind POST to ::postRentalHandler,
            )
        val request = Request(POST, "/rentals")

        val response = app(request)

        assertEquals(Status.CREATED, response.status)
        val rentalResponse = Json.decodeFromString<RentalResponse>(response.bodyString())
        assertEquals(102, rentalResponse.rid) // Check the simulated new ID
    }

    @Test
    fun `GET rentals for specific court returns OK and list`() {
        val app =
            routes(
                "/rentals/{cid}/{crid}" bind GET to ::getCourtRentalsHandler,
            )
        val targetCid = 1
        val targetCrid = 11

        val response = app(Request(GET, "/rentals/$targetCid/$targetCrid"))

        assertEquals(Status.OK, response.status)
        val rentalsList = Json.decodeFromString<List<RentalResponse>>(response.bodyString())
        assertEquals(2, rentalsList.size)
        assertTrue(rentalsList.all { it.club == targetCid && it.court == targetCrid })
        assertEquals(101, rentalsList[0].rid)
        assertEquals(103, rentalsList[1].rid)
    }

    @Test
    fun `GET rentals for specific user returns OK and list`() {
        val app =
            routes(
                "/user/{uid}/rentals" bind GET to ::getUserRentalsHandler,
            )
        val targetUid = 1

        val response = app(Request(GET, "/user/$targetUid/rentals"))

        assertEquals(Status.OK, response.status)
        val rentalsList = Json.decodeFromString<List<RentalResponse>>(response.bodyString())
        assertEquals(2, rentalsList.size) // Based on the mock handler
        assertTrue(rentalsList.all { it.uid == targetUid })
        assertEquals(101, rentalsList[0].rid)
        assertEquals(104, rentalsList[1].rid)
    }

    @Test
    fun `DELETE rental with existing ID returns OK`() {
        val app =
            routes(
                "/rentals/{rid}/delete" bind DELETE to ::deleteRentalHandler,
            )
        val existingRentalId = 101

        val response = app(Request(DELETE, "/rentals/$existingRentalId/delete"))

        assertEquals(Status.OK, response.status)
        val message = Json.decodeFromString<String>(response.bodyString())
        assertEquals("Rental Deleted", message)
    }

    @Test
    fun `DELETE rental with non-existing ID returns NOT_FOUND`() {
        val app =
            routes(
                "/rentals/{rid}/delete" bind DELETE to ::deleteRentalHandler,
            )
        val nonExistentRentalId = 999

        val response = app(Request(DELETE, "/rentals/$nonExistentRentalId/delete"))

        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("Rental not found", response.bodyString())
    }

    @Test
    fun `PUT rental with existing ID returns OK and updated rental`() {
        val app =
            routes(
                "/rentals/{rid}" bind PUT to ::updateRentalHandler,
            )
        val existingRentalId = 101
        val updateBody =
            Json.encodeToString(
                RentalUpdate(
                    date = LocalDate(2025, 11, 25),
                    startHour = 16,
                    duration = 3,
                ),
            )

        val request = Request(PUT, "/rentals/$existingRentalId").body(updateBody)
        val response = app(request)

        assertEquals(Status.OK, response.status)
        val updatedRental = Json.decodeFromString<RentalResponse>(response.bodyString())
        assertEquals(existingRentalId, updatedRental.rid)
        assertEquals(LocalDate(2025, 11, 25), updatedRental.date)
        assertEquals(16, updatedRental.starthour)
        assertEquals(3, updatedRental.duration)
    }

    @Test
    fun `PUT rental with non-existing ID returns NOT_FOUND`() {
        val app =
            routes(
                "/rentals/{rid}" bind PUT to ::updateRentalHandler,
            )
        val nonExistentRentalId = 999
        val updateBody =
            Json.encodeToString(
                RentalUpdate(
                    date = LocalDate(2025, 11, 25),
                    startHour = 16,
                    duration = 3,
                ),
            )

        val request = Request(PUT, "/rentals/$nonExistentRentalId").body(updateBody)
        val response = app(request)

        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("Rental not found", response.bodyString())
    }
}
