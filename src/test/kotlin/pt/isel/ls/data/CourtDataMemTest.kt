package pt.isel.ls.data

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import pt.isel.ls.data.mem.DataMem
import pt.isel.ls.data.models.ClubCreate
import pt.isel.ls.data.models.CourtCreate
import pt.isel.ls.data.models.UserCreate
import pt.isel.ls.utils.AppException

class CourtDataMemTest {
    // Helper method to set up a user and club for testing
    private fun setupUserAndClub(
        id: Int,
        clubName: String,
    ): Pair<Int, Int> {
        val uid = DataMem.users.createUser(UserCreate("User$id", "user$id-${System.nanoTime()}@example.com", "password")).uid
        val cid = DataMem.clubs.createClub(ClubCreate(clubName, uid)).cid
        return Pair(uid, cid)
    }

    // Helper method to assert that a block of code throws an AppException.Conflict
    private fun assertConflict(block: () -> Unit) {
        assertThrows(AppException.Conflict::class.java, block)
    }

    // Helper method to assert that a block of code throws an AppException.NotFound
    private fun assertNotFound(block: () -> Unit) {
        assertThrows(AppException.NotFound::class.java, block)
    }

    @Test
    fun `create court with valid cid and name`() {
        val (owner, club) = setupUserAndClub(1, "Climbing Club")
        val court = DataMem.courts.createCourt(CourtCreate("Main Court", club, owner)).crid
        assertNotNull(court)
    }

    @Test
    fun `reject duplicate names within the same club`() {
        val (owner, club) = setupUserAndClub(2, "Dup Club")
        DataMem.courts.createCourt(CourtCreate("Secondary Court", club, owner))
        assertConflict {
            DataMem.courts.createCourt(CourtCreate("Secondary Court", club, owner))
        }
    }

    @Test
    fun `get court details with valid cid`() {
        val (owner, club) = setupUserAndClub(3, "Details Club")
        val court = DataMem.courts.createCourt(CourtCreate("Outdoor Court", club, owner))
        val retrievedCourt = DataMem.courts.getCourtDetails(court.club, court.crid)
        assertEquals(court.crid, retrievedCourt.crid)
        assertEquals(court.name, retrievedCourt.name)
        assertEquals(court.club, retrievedCourt.club.cid)
    }

    @Test
    fun `list courts by club with valid cid`() {
        val (owner, club) = setupUserAndClub(4, "List Club")
        for (i in 1..5) {
            DataMem.courts.createCourt(CourtCreate("Court $i", club, owner))
        }
        val courts = DataMem.courts.getCourtsByClubID(club, 0, 100)
        assertEquals(5, courts.size)
    }

    @Test
    fun `invalid cid throws CourtNotFound`() {
        assertNotFound {
            DataMem.courts.getCourtDetails(999, 921)
        }
    }
}
