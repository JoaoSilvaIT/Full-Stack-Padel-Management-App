package pt.isel.ls.services

import org.junit.Before
import org.junit.Test
import pt.isel.ls.data.mem.DataMem
import pt.isel.ls.services.models.ClubInput
import pt.isel.ls.services.models.UserInput
import pt.isel.ls.utils.AppException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ClubServicesTest {
    private lateinit var services: Services

    private fun assertNotFound(block: () -> Unit) {
        assertFailsWith<AppException.NotFound> { block() }
    }

    private fun createTestUser(
        name: String,
        email: String,
        password: String,
    ): Int = services.users.createUser(UserInput(name, email, password)).uid

    @Before
    fun setup() {
        services = Services(DataMem)
    }

    @Test
    fun `create club and retrieve it by id`() {
        val ownerName = "Club Owner"
        val email = "owner1@test.com"
        val password = "password"
        val ownerId = createTestUser(ownerName, email, password)

        val input = ClubInput(name = "Padel Clube LS")
        val createdClub = services.clubs.createClub(input, ownerId)
        val retrieved = services.clubs.getClubDetails(createdClub.cid)

        assertEquals(createdClub.cid, retrieved.cid)
        assertEquals("Padel Clube LS", retrieved.name)
        assertEquals(ownerId, retrieved.owner.uid)
    }

    @Test
    fun `create multiple clubs and list them`() {
        val ownerName = "Multi Club Owner"
        val email = "multiowner@test.com"
        val password = "password"
        val ownerId = createTestUser(ownerName, email, password)

        val names = listOf("Alpha", "Beta", "Gamma")
        names.forEach { services.clubs.createClub(ClubInput(it), ownerId) }
        val allClubs = services.clubs.getClubs(0, 100)
        val clubNames = allClubs.map { it.name }

        for (name in names) {
            assertTrue(clubNames.contains(name), "Club list should contain $name")
        }
    }

    @Test
    fun `get club details by ID with invalid ID throws ClubNotFound`() {
        assertNotFound {
            services.clubs.getClubDetails(-101)
        }
    }

    @Test
    fun `get club details by name returns correct club`() {
        val ownerName = "Unique Owner"
        val email = "unique@test.com"
        val password = "password"
        val ownerId = createTestUser(ownerName, email, password)
        val name = "UniqueName"
        services.clubs.createClub(ClubInput(name), ownerId)
        val found = services.clubs.getClubDetailsByName(name)

        assertEquals(name, found.name)
        assertEquals(ownerId, found.ownerId)
    }

    @Test
    fun `get club details by name with invalid name throws ClubNotFound`() {
        assertNotFound {
            services.clubs.getClubDetailsByName("NonExistent")
        }
    }
}
