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
import pt.isel.ls.api.models.ClubDetails
import pt.isel.ls.api.models.ClubResponse
import pt.isel.ls.api.models.CourtDraft
import pt.isel.ls.api.models.UserDraft
import kotlin.test.Test
import kotlin.test.assertEquals

class ClubTest {
    private fun getAllClubsHandler(request: Request): Response {
        val mockClubsList =
            listOf(
                ClubResponse(cid = 1, name = "Padel Central", ownerId = 1),
                ClubResponse(cid = 2, name = "Top Spin Club", ownerId = 2),
            )
        return Response(Status.OK).body(Json.encodeToString(mockClubsList))
    }

    private fun getClubDetailsHandler(request: Request): Response {
        val cid = request.path("cid")?.toIntOrNull()
        return if (cid == 1) {
            val mockClubDetails =
                ClubDetails(
                    cid = 1,
                    name = "Padel Central Detailed",
                    owner = UserDraft(uid = 1, name = "Mock Owner One"),
                    courts =
                        listOf(
                            CourtDraft(cid = 131, crid = 11, name = "Court Alpha"),
                            CourtDraft(cid = 123, crid = 12, name = "Court Beta"),
                        ),
                )
            Response(Status.OK).body(Json.encodeToString(mockClubDetails))
        } else {
            Response(Status.NOT_FOUND).body("Club not found")
        }
    }

    private fun postClubHandler(request: Request): Response {
        val mockClubResponse =
            ClubResponse(
                cid = 3,
                name = "Newly Mocked Club",
                ownerId = 1,
            )
        return Response(Status.CREATED).body(Json.encodeToString(mockClubResponse))
    }

    @Test
    fun `GET all clubs returns OK and list`() {
        val app =
            routes(
                "/clubs" bind GET to ::getAllClubsHandler,
            )

        val response = app(Request(GET, "/clubs"))
        assertEquals(Status.OK, response.status)
        val clubsList = Json.decodeFromString<List<ClubResponse>>(response.bodyString())
        assertEquals(2, clubsList.size)
        assertEquals(1, clubsList[0].cid)
        assertEquals("Padel Central", clubsList[0].name)
        assertEquals(2, clubsList[1].cid)
        assertEquals("Top Spin Club", clubsList[1].name)
    }

    @Test
    fun `GET club details for existing club returns OK`() {
        val app =
            routes(
                "/clubs/{cid}" bind GET to ::getClubDetailsHandler,
            )
        val targetCid = 1

        val response = app(Request(GET, "/clubs/$targetCid"))

        assertEquals(Status.OK, response.status)
        val clubDetails = Json.decodeFromString<ClubDetails>(response.bodyString())
        assertEquals(targetCid, clubDetails.cid)
        assertEquals("Padel Central Detailed", clubDetails.name)
        assertEquals(1, clubDetails.owner.uid)
        assertEquals(2, clubDetails.courts.size)
        assertEquals(11, clubDetails.courts[0].crid)
    }

    @Test
    fun `GET club details for non-existing club returns NOT_FOUND`() {
        val app =
            routes(
                "/clubs/{cid}" bind GET to ::getClubDetailsHandler,
            )
        val nonExistentCid = 999

        val response = app(Request(GET, "/clubs/$nonExistentCid"))

        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("Club not found", response.bodyString())
    }

    @Test
    fun `POST club returns CREATED`() {
        val app =
            routes(
                "/clubs" bind POST to ::postClubHandler,
            )
        val request = Request(POST, "/clubs")

        val response = app(request)

        assertEquals(Status.CREATED, response.status)
        val clubResponse = Json.decodeFromString<ClubResponse>(response.bodyString())
        assertEquals(3, clubResponse.cid)
        assertEquals("Newly Mocked Club", clubResponse.name)
        assertEquals(1, clubResponse.ownerId)
    }
}
