package pt.isel.ls.data

import kotlinx.datetime.LocalDate
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import pt.isel.ls.data.mem.DataMem
import pt.isel.ls.data.models.ClubCreate
import pt.isel.ls.data.models.CourtCreate
import pt.isel.ls.data.models.RentalCreate
import pt.isel.ls.data.models.UserCreate
import pt.isel.ls.utils.AppException

class RentalsDataMemTest {
    // Helper method to set up a user, club, and court for testing
    private fun setupUserClubCourt(
        id: Int,
        clubName: String,
        courtName: String,
    ): Triple<Int, Int, Int> {
        val uid = DataMem.users.createUser(UserCreate("User$id", "user$id-${System.nanoTime()}@example.com", "password")).uid
        val cid = DataMem.clubs.createClub(ClubCreate(clubName, uid)).cid
        val crid = DataMem.courts.createCourt(CourtCreate(courtName, cid, uid)).crid
        return Triple(uid, cid, crid)
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
    fun `create rental with valid cid, crid, date, and duration`() {
        val (uid, cid, crid) = setupUserClubCourt(1, "Club1", "Court1")
        val date = LocalDate(2026, 12, 25)
        val rental = DataMem.rentals.createRental(RentalCreate(cid, crid, date, 10, 2, uid))
        assertNotNull(rental.rid, "Rental ID should not be null")
        assertEquals(cid, rental.club)
        assertEquals(crid, rental.court)
        assertEquals(date, rental.date)
        assertEquals(10, rental.starthour)
        assertEquals(2, rental.duration)
        assertEquals(uid, rental.uid)
    }

    @Test
    fun `get rental details with valid rid`() {
        val (uid, cid, crid) = setupUserClubCourt(2, "Club2", "Court2")
        val date = LocalDate(2026, 12, 25)
        val rental = DataMem.rentals.createRental(RentalCreate(cid, crid, date, 10, 2, uid))
        val retrievedRental = DataMem.rentals.getRentalDetails(rental.club, rental.court, rental.rid)
        assertEquals(rental.rid, retrievedRental.rid)
        assertEquals(rental.club, retrievedRental.court.cid)
        assertEquals(rental.court, retrievedRental.court.crid)
        assertEquals(rental.date, retrievedRental.date)
        assertEquals(rental.starthour, retrievedRental.starthour)
        assertEquals(rental.duration, retrievedRental.duration)
        assertEquals(rental.uid, retrievedRental.renter.uid)
    }

    @Test
    fun `list rentals by club and court`() {
        val (uid, cid, crid) = setupUserClubCourt(3, "Club3", "Court3")
        val date = LocalDate(2027, 11, 25)

        val userIds =
            (1..5).map { i ->
                DataMem.users.createUser(UserCreate("RentalUser$i", "rentaluser$i-${System.nanoTime()}@example.com", "password")).uid
            }
        for (i in 1..5) {
            DataMem.rentals.createRental(RentalCreate(cid, crid, date, 10 + i, 1, userIds[i - 1]))
        }
        val (uid2, cid2, crid2) = setupUserClubCourt(30, "Club30", "Court30")
        DataMem.rentals.createRental(RentalCreate(cid2, crid2, date, 12, 1, uid2))

        val rentals = DataMem.rentals.getRentals(cid, crid, null, 0, 100)
        assertEquals(5, rentals.size)
    }

    @Test
    fun `list rentals by date`() {
        val (uid, cid, crid) = setupUserClubCourt(4, "Club4", "Court4")
        val date1 = LocalDate(2028, 12, 25)
        val date2 = LocalDate(2028, 12, 26)

        for (i in 1..5) {
            DataMem.rentals.createRental(RentalCreate(cid, crid, date1, 10 + i, 1, uid))
        }
        for (i in 1..2) {
            DataMem.rentals.createRental(RentalCreate(cid, crid, date2, 10 + i, 1, uid))
        }

        val rentals = DataMem.rentals.getRentals(cid, crid, date1, 0, 100)
        assertEquals(5, rentals.size)
        assertTrue(rentals.all { it.date == date1 })
    }

    @Test
    fun `list rentals by user`() {
        val (uid1, cid1, crid1) = setupUserClubCourt(1213, "User1213Club", "User1213Court")
        val (uid2, cid2, crid2) = setupUserClubCourt(6, "User6Club", "User6Court")
        val date = LocalDate(2029, 10, 27)

        for (i in 1..3) {
            DataMem.rentals.createRental(RentalCreate(cid1, crid1, date, 10 + i, 1, uid1))
        }
        for (i in 1..2) {
            DataMem.rentals.createRental(RentalCreate(cid2, crid2, date, 10 + i, 1, uid2))
        }

        val rentals = DataMem.rentals.getRentalsByUser(uid2, 0, 100)
        assertEquals(2, rentals.size)
        assertTrue(rentals.all { it.uid == uid2 })
    }

    @Test
    fun `get available hours for valid cid, crid, and date`() {
        val (uid1, cid1, crid1) = setupUserClubCourt(12, "AHClub", "AHCourt")
        val date = LocalDate(2030, 12, 25)

        DataMem.rentals.createRental(RentalCreate(cid1, crid1, date, 10, 2, uid1))
        DataMem.rentals.createRental(RentalCreate(cid1, crid1, date, 14, 1, uid1))

        val availableHours = DataMem.rentals.getAvailableRentHours(cid1, crid1, date)
        val totalHours = 24
        val bookedHoursCount = 3 // 10, 11 and 14 are booked

        assertEquals(totalHours - bookedHoursCount, availableHours.size, "Should have ${totalHours - bookedHoursCount} available hours")
        assertTrue(availableHours.contains(9), "Hour 9 should be available")
        assertTrue(!availableHours.contains(10), "Hour 10 should NOT be available")
        assertTrue(!availableHours.contains(11), "Hour 11 should NOT be available")
        assertTrue(availableHours.contains(12), "Hour 12 should be available")
        assertTrue(availableHours.contains(13), "Hour 13 should be available")
        assertTrue(!availableHours.contains(14), "Hour 14 should NOT be available")
        assertTrue(availableHours.contains(15), "Hour 15 should be available")
    }

    @Test
    fun `create rental with past date throws IllegalArgumentException`() {
        val (uid, cid, crid) = setupUserClubCourt(5, "PastClub", "PastCourt")
        val pastDate = LocalDate(2020, 12, 25)
        // Using standard assertThrows for IllegalArgumentException since it's not an AppException
        assertThrows(IllegalArgumentException::class.java) {
            DataMem.rentals.createRental(RentalCreate(cid, crid, pastDate, 10, 2, uid))
        }
    }

    @Test
    fun `create rental with duration zero throws IllegalArgumentException`() {
        val (uid, cid, crid) = setupUserClubCourt(6, "ZeroDurationClub", "ZeroDurationCourt")
        val date = LocalDate(2042, 12, 25)
        // Using standard assertThrows for IllegalArgumentException since it's not an AppException
        assertThrows(IllegalArgumentException::class.java) {
            DataMem.rentals.createRental(RentalCreate(cid, crid, date, 20, 0, uid))
        }
    }

    @Test
    fun `test overlapping rentals throws RentalConflict`() {
        // Create a club/court and two users
        val (uid1, cid, crid) = setupUserClubCourt(12, "blablabla", "12awd3ar")
        val uid2 = DataMem.users.createUser(UserCreate("User13", "user13_unique@example.com", "password")).uid
        val date = LocalDate(2028, 12, 25)

        // First rental on cid/crid at 10 for 2 hours (10-12)
        DataMem.rentals.createRental(RentalCreate(cid, crid, date, 10, 2, uid1))

        // Second rental tries to overlap (11-12)
        assertConflict {
            DataMem.rentals.createRental(RentalCreate(cid, crid, date, 11, 1, uid2))
        }
    }

    @Test
    fun `get rental details for invalid rid throws RentalNotFound`() {
        assertNotFound {
            DataMem.rentals.getRentalDetails(9999, 9999, 9999)
        }
    }
}
