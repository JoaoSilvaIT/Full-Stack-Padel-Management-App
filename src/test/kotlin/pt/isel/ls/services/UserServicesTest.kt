package pt.isel.ls.services

import org.junit.Before
import org.junit.Test
import pt.isel.ls.data.mem.DataMem
import pt.isel.ls.services.models.UserInput
import pt.isel.ls.utils.AppException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UserServicesTest {
    private lateinit var services: Services

    // Helper method to assert that a block of code throws an AppException.NotFound
    private fun assertNotFound(block: () -> Unit) {
        assertFailsWith<AppException.NotFound> { block() }
    }

    @Before
    fun setup() {
        services = Services(DataMem)
    }

    @Test
    fun `create user with valid name and email`() {
        val input = UserInput("John Doe", "john@example.com", "password")
        val result = services.users.createUser(input)
        assertEquals("John Doe", result.name)
        assertEquals("john@example.com", result.mail)
    }

    @Test
    fun `get user details with valid uid`() {
        val input = UserInput("John Doe", "john.doe@example.com", "password")
        val createdUser = services.users.createUser(input)
        val result = services.users.getUserDetails(createdUser.uid)
        assertEquals(createdUser.uid, result.uid)
        assertEquals(createdUser.name, result.name)
    }

    @Test
    fun `get user details with invalid uid throws UserNotFound`() {
        assertNotFound {
            services.users.getUserDetails(999)
        }
    }
}
