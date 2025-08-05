package pt.isel.ls.services

import kotlinx.datetime.LocalDate
import org.junit.Before
import org.junit.Test
import pt.isel.ls.data.mem.DataMem
import pt.isel.ls.services.models.ClubInput
import pt.isel.ls.services.models.CourtInput
import pt.isel.ls.services.models.RentalInput
import pt.isel.ls.services.models.UserInput
import pt.isel.ls.utils.AppException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RentalServicesTest {
    private lateinit var services: Services

    private fun assertNotFound(block: () -> Unit) { // Helper method to assert that a block of code throws an AppException.NotFound
        assertFailsWith<AppException.NotFound> { block() }
    }

    private fun assertInvalidData(block: () -> Unit) { // Helper method to assert that a block of code throws an AppException.InvalidData
        assertFailsWith<AppException.InvalidData> { block() }
    }

    private fun createTestUser(
        name: String,
        email: String,
        password: String,
    ): Int = services.users.createUser(UserInput(name, email, password)).uid

    private fun createTestClub(
        name: String,
        ownerId: Int,
    ): Int = services.clubs.createClub(ClubInput(name), ownerId).cid

    // Helper method to create a test court
    private fun createTestCourt(
        name: String,
        clubId: Int,
        ownerId: Int,
    ): Int = services.courts.createCourt(CourtInput(name, clubId), ownerId).crid

    // Helper method to set up a user, club, and court for testing
    private fun setupUserClubCourt(
        userName: String,
        clubName: String,
        courtName: String,
        email: String,
        password: String,
    ): Triple<Int, Int, Int> {
        val uid = createTestUser(userName, email, password)
        val cid = createTestClub(clubName, uid)
        val crid = createTestCourt(courtName, cid, uid)
        return Triple(uid, cid, crid)
    }

    @Before
    fun setup() {
        services = Services(DataMem)
    }

    @Test
    fun `create rental and retrieve it by id`() {
        val (user, club, court) = setupUserClubCourt("Rental User", "Rental Club", "Rental Court", "rental1@example.com", "password")

        val date = LocalDate(2026, 4, 16)
        val starthour = 10
        val duration = 1

        val rentalInput = RentalInput(club, court, date, starthour, duration)
        val createdRental = services.rentals.createRental(rentalInput, user)
        val retrieved = services.rentals.getRentalDetails(createdRental.club, createdRental.court, createdRental.rid)

        assertEquals(createdRental.rid, retrieved.rid)
        assertEquals(club, retrieved.court.cid)
        assertEquals(court, retrieved.court.crid)
        assertEquals(date, retrieved.date)
        assertEquals(starthour, retrieved.starthour)
        assertEquals(duration, retrieved.duration)
        assertEquals(user, retrieved.renter.uid)
    }

    @Test
    fun `create multiple rentals and list them`() {
        val (user, club, court) =
            setupUserClubCourt(
                "Multi Rental User",
                "Multi Rental Club",
                "Multi Rental Court",
                "multi@example.com",
                "password",
            )

        val date = LocalDate(2054, 4, 16)
        val rentals =
            listOf(
                RentalInput(club, court, date, 12, 1),
                RentalInput(club, court, date, 13, 1),
            )
        rentals.forEach { services.rentals.createRental(it, user) }
        val allRentals = services.rentals.getRentals(club, court, date.toString(), 0, 100)

        assertEquals(2, allRentals.size)
        assertTrue(allRentals.any { it.starthour == 12 })
        assertTrue(allRentals.any { it.starthour == 13 })
    }

    @Test
    fun `get rental details by ID with invalid ID throws RentalNotFound`() {
        assertNotFound {
            services.rentals.getRentalDetails(123, 213, 999)
        }
    }

    @Test
    fun `get rentals by user ID returns correct rentals`() {
        val (user, club, court) =
            setupUserClubCourt(
                "User Rentals",
                "User Rentals Club",
                "User Rentals Court",
                "user@rentals.com",
                "password",
            )

        val date = LocalDate(2029, 4, 16)
        val starthour = 15
        val duration = 1

        services.rentals.createRental(RentalInput(club, court, date, starthour, duration), user)
        val userRentals = services.rentals.getRentalsByUser(user, 0, 100)

        assertTrue(userRentals.isNotEmpty())
        assertEquals(user, userRentals.first().uid)
    }

    @Test
    fun `get rentals by court ID returns correct rentals`() {
        val (user, club, court) =
            setupUserClubCourt(
                "Court Rentals User",
                "Court Rentals Club",
                "Court Rentals Court",
                "court@rental.com",
                "password",
            )

        val date = LocalDate(2027, 4, 16)
        val starthour = 17
        val duration = 1

        services.rentals.createRental(RentalInput(club, court, date, starthour, duration), user)
        val courtRentals = services.rentals.getRentals(club, court, date.toString(), 0, 100)

        assertTrue(courtRentals.isNotEmpty())
        assertEquals(court, courtRentals.first().court)
    }

    @Test
    fun `delete rental removes rental from system`() {
        val (user, club, court) =
            setupUserClubCourt(
                "Delete Rental User",
                "Delete Rental Club",
                "Delete Rental Court",
                "delete@rental.com",
                "password",
            )

        val date = LocalDate(2028, 5, 20)
        val starthour = 14
        val duration = 2

        val rentalInput = RentalInput(club, court, date, starthour, duration)
        val createdRental = services.rentals.createRental(rentalInput, user)

        // Verify rental exists
        val rentals = services.rentals.getRentals(club, court, date.toString(), 0, 100)
        assertTrue(rentals.any { it.rid == createdRental.rid })

        // Delete rental
        services.rentals.deleteRental(createdRental.rid)

        // Verify rental no longer exists
        assertNotFound {
            services.rentals.getRentalDetails(createdRental.club, createdRental.court, createdRental.rid)
        }
    }

    @Test
    fun `update rental changes rental details`() {
        val (user, club, court) =
            setupUserClubCourt(
                "Update Rental User",
                "Update Rental Club",
                "Update Rental Court",
                "update@rental.com",
                "password",
            )

        val originalDate = LocalDate(2028, 6, 15)
        val originalStartHour = 10
        val originalDuration = 1

        val rentalInput = RentalInput(club, court, originalDate, originalStartHour, originalDuration)
        val createdRental = services.rentals.createRental(rentalInput, user)

        // Update rental
        val newDate = LocalDate(2028, 7, 20)
        val newStartHour = 14
        val newDuration = 2

        val updatedRental =
            services.rentals.updateRental(
                createdRental.rid,
                newDate,
                newStartHour,
                newDuration,
            )

        // Verify rental was updated
        assertEquals(createdRental.rid, updatedRental.rid)
        assertEquals(newDate, updatedRental.date)
        assertEquals(newStartHour, updatedRental.starthour)
        assertEquals(newDuration, updatedRental.duration)

        // Verify the changes are persisted
        val retrievedRental = services.rentals.getRentalDetails(createdRental.club, createdRental.court, createdRental.rid)
        assertEquals(newDate, retrievedRental.date)
        assertEquals(newStartHour, retrievedRental.starthour)
        assertEquals(newDuration, retrievedRental.duration)
    }

    @Test
    fun `update rental with invalid data throws InvalidData`() {
        val (user, club, court) =
            setupUserClubCourt(
                "Invalid Update User",
                "Invalid Update Club",
                "Invalid Update Court",
                "invalid@update.com",
                "password",
            )

        val date = LocalDate(2028, 8, 10)
        val starthour = 16
        val duration = 1

        val rentalInput = RentalInput(club, court, date, starthour, duration)
        val createdRental = services.rentals.createRental(rentalInput, user)

        // Try to update with invalid duration
        assertInvalidData {
            services.rentals.updateRental(
                createdRental.rid,
                date,
                starthour,
                -1,
            )
        }
    }
}
