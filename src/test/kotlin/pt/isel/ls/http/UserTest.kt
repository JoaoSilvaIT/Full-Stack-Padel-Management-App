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
import pt.isel.ls.api.models.ClubDraft
import pt.isel.ls.api.models.RentalDraft
import pt.isel.ls.api.models.UserDetails
import pt.isel.ls.api.models.UserResponse
import pt.isel.ls.data.models.Email
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class UserTest {
    private fun getUserDetailsHandler(request: Request): Response {
        val uid = request.path("uid")?.toIntOrNull()
        return if (uid == 1) {
            val mockUserDetails =
                UserDetails(
                    uid = 1,
                    name = "Mock User One",
                    clubs = listOf(ClubDraft(cid = 101, name = "Mock Club A")),
                    rentals = listOf(RentalDraft(rid = 171, uid = 148)),
                )
            Response(Status.OK).body(Json.encodeToString(mockUserDetails))
        } else {
            Response(Status.NOT_FOUND).body("User not found")
        }
    }

    private fun postUserHandler(request: Request): Response {
        val mockUserResponse =
            UserResponse(
                token = UUID.randomUUID(),
                name = "Newly Mocked User",
                email = Email("mocked@example.com"),
                uid = 2,
            )
        return Response(Status.CREATED).body(Json.encodeToString(mockUserResponse))
    }

    @Test
    fun `GET user with existing ID returns OK and user details`() {
        val app =
            routes(
                "/user/{uid}" bind GET to ::getUserDetailsHandler,
                "/user" bind POST to ::postUserHandler,
            )

        val expectedUserId = 1

        val response =
            app(
                Request(GET, "/user/$expectedUserId"),
            )

        assertEquals(Status.OK, response.status)

        val userDetails = Json.decodeFromString<UserDetails>(response.bodyString())

        assertEquals(expectedUserId, userDetails.uid)
        assertEquals("Mock User One", userDetails.name)
        assertEquals(1, userDetails.clubs.size)
        assertEquals(101, userDetails.clubs[0].cid)
        assertEquals("Mock Club A", userDetails.clubs[0].name)
    }

    @Test
    fun `GET user with non-existing ID returns NOT_FOUND`() {
        val app =
            routes(
                "/user/{uid}" bind GET to ::getUserDetailsHandler,
                "/user" bind POST to ::postUserHandler,
            )
        val nonExistentUserId = 999

        val response = app(Request(GET, "/user/$nonExistentUserId"))

        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("User not found", response.bodyString())
    }
}
