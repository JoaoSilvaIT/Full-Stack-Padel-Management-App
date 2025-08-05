package pt.isel.ls.data

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import pt.isel.ls.data.mem.DataMem
import pt.isel.ls.data.models.ClubCreate
import pt.isel.ls.data.models.UserCreate
import pt.isel.ls.utils.AppException

class ClubDataMemTest {
    // Helper method to create a user for testing
    private fun createTestUser(id: Int): Int =
        DataMem.users.createUser(UserCreate("User$id", "user$id-${System.nanoTime()}@example.com", "password")).uid

    // Helper method to assert that a block of code throws an AppException.Conflict
    private fun assertConflict(block: () -> Unit) {
        assertThrows(AppException.Conflict::class.java, block)
    }

    // Helper method to assert that a block of code throws an AppException.NotFound
    private fun assertNotFound(block: () -> Unit) {
        assertThrows(AppException.NotFound::class.java, block)
    }

    @Test
    fun `create club with valid name and owner token`() {
        val uid = createTestUser(1)
        val club = DataMem.clubs.createClub(ClubCreate("Futebol Club", uid))
        assertNotNull(club.cid)
    }

    @Test
    fun `reject duplicate names`() {
        val uid1 = createTestUser(2)
        val uid2 = createTestUser(3)
        DataMem.clubs.createClub(ClubCreate("Tennis Club", uid1))
        assertConflict {
            DataMem.clubs.createClub(ClubCreate("Tennis Club", uid2))
        }
    }

    @Test
    fun `get club details with valid cid`() {
        val uid = createTestUser(4)
        val club = DataMem.clubs.createClub(ClubCreate("Basket Club", uid))
        val retrievedClub = DataMem.clubs.getClubDetails(club.cid)
        assertEquals(club.cid, retrievedClub.cid)
        assertEquals(club.name, retrievedClub.name)
        assertEquals(club.ownerId, retrievedClub.owner.uid)
    }

    @Test
    fun `list clubs returns all clubs`() {
        val initialCount = DataMem.clubs.getClubs(skip = 0, limit = 100).size
        val uid = createTestUser(10)

        for (i in 1..5) {
            DataMem.clubs.createClub(ClubCreate(("Club $i"), uid))
        }
        val clubs = DataMem.clubs.getClubs(skip = 0, limit = 100)
        assertEquals(initialCount + 5, clubs.size)
    }

    @Test
    fun `list clubs contains created clubs`() {
        val uid = createTestUser(6)
        val club = DataMem.clubs.createClub(ClubCreate("Unique Test Club", uid))
        val clubs = DataMem.clubs.getClubs(skip = 0, limit = 100)
        assertTrue(clubs.any { it.cid == club.cid && it.name == club.name })
    }

    @Test
    fun `missing or invalid token throws UserNotFound`() {
        assertNotFound {
            DataMem.clubs.createClub(ClubCreate("Rugby Club", 123123))
        }
    }

    @Test
    fun `invalid cid throws ClubNotFound`() {
        assertNotFound {
            DataMem.clubs.getClubDetails(999)
        }
    }
}
