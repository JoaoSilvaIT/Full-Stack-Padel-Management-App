package pt.isel.ls.data

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import pt.isel.ls.data.mem.DataMem
import pt.isel.ls.data.models.UserCreate
import pt.isel.ls.utils.AppException

class UserDataMemTest {
    // Helper method to assert that a block of code throws an AppException.InvalidData
    private fun assertInvalidData(block: () -> Unit) {
        assertThrows(AppException.InvalidData::class.java, block)
    }

    // Helper method to assert that a block of code throws an AppException.NotFound
    private fun assertNotFound(block: () -> Unit) {
        assertThrows(AppException.NotFound::class.java, block)
    }

    @Test
    fun `create user with valid name and email`() {
        val user = DataMem.users.createUser(UserCreate("Alice", "alice@example.com", "password"))
        assertNotNull(user.uid)
        assertNotNull(user.token)
    }

    @Test
    fun `reject duplicate emails`() {
        assertInvalidData {
            DataMem.users.createUser(UserCreate("Rodrigo", "rodrigo@example.com", "password"))
            DataMem.users.createUser(UserCreate("Bob", "rodrigo@example.com", "password"))
        }
    }

    @Test
    fun `get user details with valid uid`() {
        val user = DataMem.users.createUser(UserCreate("Joao", "joao@example.com", "password"))
        val retrievedUser = DataMem.users.getUser(user.uid)
        assertEquals(user.uid, retrievedUser.uid)
        assertEquals(user.name, retrievedUser.name)
        assertEquals(user.email.value, retrievedUser.email.value)
    }

    @Test
    fun `token validation`() {
        val user = DataMem.users.createUser(UserCreate("Goncalo", "goncalo@example.com", "password"))
        val retrievedUser = DataMem.users.getUser(user.uid)
        assertEquals(user.token, retrievedUser.token)
    }

    @Test
    fun `malformed email throws EmailNotValid`() {
        assertInvalidData {
            DataMem.users.createUser(UserCreate("Alice", "user@", "password"))
        }
    }

    @Test
    fun `invalid uid throws UserNotFound`() {
        assertNotFound {
            DataMem.users.getUser(999)
        }
    }
}
