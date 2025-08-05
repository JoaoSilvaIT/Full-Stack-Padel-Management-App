package pt.isel.ls.sql

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.postgresql.ds.PGSimpleDataSource
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import kotlin.test.assertEquals

class AddDataTest {
    private lateinit var connection: Connection

    @Before
    fun setup() {
        connection = createDatabaseConnection()
        executeScript("src/main/sql/createSchema.sql")
        executeScript("src/main/sql/addData.sql")
    }

    @After
    fun teardown() {
        dropSchema()
        connection.close()
    }

    private fun createDatabaseConnection(): Connection {
        val user = System.getenv("USER")
        val password = System.getenv("PASSWORD")
        val dataSource = PGSimpleDataSource()
        dataSource.setURL("jdbc:postgresql://localhost/postgres?user=$user&password=$password")
        return dataSource.connection
    }

    private fun executeScript(scriptPath: String) {
        val scriptContent = Files.readString(Paths.get(scriptPath))
        connection.createStatement().use { stmt ->
            stmt.execute(scriptContent)
        }
    }

    private fun dropSchema() {
        connection.createStatement().use { stmt ->
            stmt.execute("DROP SCHEMA public CASCADE; CREATE SCHEMA public;")
        }
    }

    private fun getTableCount(tableName: String): Int {
        connection.createStatement().use { stmt ->
            val rs = stmt.executeQuery("SELECT COUNT(*) FROM $tableName")
            rs.next()
            return rs.getInt(1)
        }
    }

    @Test
    fun testUsersData() {
        assertEquals(3, getTableCount("Users"))
    }

    @Test
    fun testClubsData() {
        assertEquals(3, getTableCount("Clubs"))
    }

    @Test
    fun testCourtsData() {
        assertEquals(4, getTableCount("Courts"))
    }

    @Test
    fun testRentalsData() {
        assertEquals(3, getTableCount("Rentals"))
    }
}
