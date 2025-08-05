package pt.isel.ls.http

import kotlinx.serialization.json.Json
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import pt.isel.ls.api.models.ClubResponse
import pt.isel.ls.api.models.CourtDetails
import pt.isel.ls.api.models.CourtResponse
import pt.isel.ls.api.models.RentalDraft
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CourtTest {
    private fun getCourtsByClubHandler(request: Request): Response {
        val cid = request.path("cid")?.toIntOrNull()
        return if (cid == 1) {
            val mockCourtsList =
                listOf(
                    CourtResponse(name = "Mock Court A", club = 1, crid = 11),
                    CourtResponse(name = "Mock Court B", club = 1, crid = 12),
                )
            Response(Status.OK).body(Json.encodeToString(mockCourtsList))
        } else {
            Response(Status.OK).body(Json.encodeToString(emptyList<CourtResponse>()))
        }
    }

    private fun getCourtDetailsHandler(request: Request): Response {
        val cid = request.path("cid")?.toIntOrNull()
        val crid = request.path("crid")?.toIntOrNull()

        if (cid == 1 && crid == 11) {
            val mockCourtDetails =
                CourtDetails(
                    crid = 11,
                    name = "Mock Court Alpha Detailed",
                    club = ClubResponse(cid = 1, name = "Mock Club A", ownerId = 1),
                    rentals =
                        listOf(
                            RentalDraft(rid = 101, uid = 112),
                            RentalDraft(rid = 103, uid = 142),
                        ),
                )
            return Response(Status.OK).body(Json.encodeToString(mockCourtDetails))
        } else {
            return Response(Status.NOT_FOUND).body("Court not found")
        }
    }

    private fun postCourtHandler(request: Request): Response {
        val cid = request.path("cid")?.toIntOrNull()

        if (cid == 1) {
            val mockCourtResponse =
                CourtResponse(
                    name = "Newly Mocked Court",
                    club = cid,
                    crid = 13,
                )
            return Response(Status.CREATED).body(Json.encodeToString(mockCourtResponse))
        } else {
            return Response(Status.NOT_FOUND).body("Club not found to create court in")
        }
    }

    @Test
    fun `GET courts for existing club returns OK and list`() {
        val app =
            routes(
                "/clubs/{cid}" bind
                    routes(
                        "/courts" bind GET to ::getCourtsByClubHandler,
                    ),
            )
        val targetCid = 1

        val response = app(Request(GET, "/clubs/$targetCid/courts"))

        assertEquals(Status.OK, response.status)
        val courtsList = Json.decodeFromString<List<CourtResponse>>(response.bodyString())
        assertEquals(2, courtsList.size) // Based on the mock handler
        assertTrue(courtsList.all { it.club == targetCid })
        assertEquals(11, courtsList[0].crid)
        assertEquals("Mock Court A", courtsList[0].name)
        assertEquals(12, courtsList[1].crid)
        assertEquals("Mock Court B", courtsList[1].name)
    }

    @Test
    fun `GET court details for existing court returns OK`() {
        val app =
            routes(
                "/clubs/{cid}/courts/{crid}" bind GET to ::getCourtDetailsHandler,
            )
        val targetCid = 1
        val targetCrid = 11 // The Court ID our mock handler knows

        val response = app(Request(GET, "/clubs/$targetCid/courts/$targetCrid"))

        assertEquals(Status.OK, response.status)
        val courtDetails = Json.decodeFromString<CourtDetails>(response.bodyString())
        assertEquals(targetCrid, courtDetails.crid)
        assertEquals(targetCid, courtDetails.club.cid)
        assertEquals("Mock Court Alpha Detailed", courtDetails.name)
        assertEquals(2, courtDetails.rentals.size) // Check nested data
        assertEquals(101, courtDetails.rentals[0].rid)
    }

    @Test
    fun `GET court details for non-existing court returns NOT_FOUND`() {
        val app =
            routes(
                "/clubs/{cid}/courts/{crid}" bind GET to ::getCourtDetailsHandler,
            )
        val targetCid = 1
        val nonExistentCrid = 999

        val response = app(Request(GET, "/clubs/$targetCid/courts/$nonExistentCrid"))

        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("Court not found", response.bodyString())
    }

    @Test
    fun `POST court to existing club returns CREATED`() {
        val app =
            routes(
                "/clubs/{cid}/court" bind POST to ::postCourtHandler,
            )
        val targetCid = 1
        val request = Request(POST, "/clubs/$targetCid/court")

        val response = app(request)

        assertEquals(Status.CREATED, response.status)
        val courtResponse = Json.decodeFromString<CourtResponse>(response.bodyString())
        assertEquals(13, courtResponse.crid)
        assertEquals(targetCid, courtResponse.club)
        assertEquals("Newly Mocked Court", courtResponse.name)
    }

    @Test
    fun `POST court to non-existing club returns NOT_FOUND`() {
        val app =
            routes(
                "/clubs/{cid}/court" bind POST to ::postCourtHandler,
            )
        val nonExistentCid = 999
        val request = Request(POST, "/clubs/$nonExistentCid/court")

        val response = app(request)

        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("Club not found to create court in", response.bodyString())
    }
}
