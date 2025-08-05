package pt.isel.ls.services

import org.junit.Before
import org.junit.Test
import pt.isel.ls.data.mem.DataMem
import pt.isel.ls.services.models.ClubInput
import pt.isel.ls.services.models.CourtInput
import pt.isel.ls.services.models.UserInput
import pt.isel.ls.utils.AppException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CourtServicesTest {
    private lateinit var services: Services

    // Helper method to assert that a block of code throws an AppException.NotFound
    private fun assertNotFound(block: () -> Unit) {
        assertFailsWith<AppException.NotFound> { block() }
    }

    // Helper method to create a test user
    private fun createTestUser(
        name: String,
        email: String,
        password: String,
    ): Int = services.users.createUser(UserInput(name, email, password)).uid

    // Helper method to create a test club
    private fun createTestClub(
        name: String,
        ownerId: Int,
    ): Int = services.clubs.createClub(ClubInput(name), ownerId).cid

    @Before
    fun setup() {
        services = Services(DataMem)
    }

    @Test
    fun `create court and retrieve it by id`() {
        val userName = "Test User"
        val clubName = "Golf Club"
        val courtName = "Golf Court"
        val email = "test@example.com"

        val password = "password"
        val user = createTestUser(userName, email, password)
        val club = createTestClub(clubName, user)
        val court = services.courts.createCourt(CourtInput(courtName, club), user).crid

        val courts = services.courts.getCourtsByClubID(club, 0, 100)
        assertTrue(courts.any { it.crid == court && it.name == courtName && it.club == club })
    }

    @Test
    fun `create multiple courts and list them`() {
        val userName = "Multi Court User"
        val clubName = "Multi Club"
        val email = "multicourt@example.com"

        val password = "password"
        val user = createTestUser(userName, email, password)
        val club = createTestClub(clubName, user)

        val names = listOf("Alpha Court", "Beta Court", "Gamma Court")
        names.forEach { services.courts.createCourt(CourtInput(it, club), user) }
        val allCourts = services.courts.getCourtsByClubID(club, 0, 100)
        val courtNames = allCourts.map { it.name }

        for (name in names) {
            assertTrue(courtNames.contains(name), "Courts list should contain $name")
        }
    }

    @Test
    fun `get court details by ID with invalid ID throws CourtNotFound`() {
        assertNotFound {
            services.courts.getCourtDetails(99, -101)
        }
    }

    @Test
    fun `get courts by club ID returns correct courts`() {
        val userName = "Club Courts User"
        val clubName = "Club for Courts"
        val email = "clubcourts@example.com"

        val password = "password"
        val user = createTestUser(userName, email, password)
        val club = createTestClub(clubName, user)

        val names = listOf("One", "Two")
        names.forEach { services.courts.createCourt(CourtInput(it, club), user) }
        val courts = services.courts.getCourtsByClubID(club, 0, 100)
        val courtNames = courts.map { it.name }

        assertEquals(names.size, courts.size)
        for (name in names) {
            assertTrue(courtNames.contains(name))
        }
    }
}
