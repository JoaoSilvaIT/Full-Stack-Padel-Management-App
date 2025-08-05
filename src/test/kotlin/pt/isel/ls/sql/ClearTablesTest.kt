package pt.isel.ls.sql

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.postgresql.ds.PGSimpleDataSource
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import kotlin.test.assertEquals

class ClearTablesTest {
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
        val env = System.getenv()
        val user = env["DB_USER"] ?: "postgres"
        val password = env["DB_PASSWORD"] ?: "postgres"
        val dataSource = PGSimpleDataSource()
        dataSource.setURL("jdbc:postgresql://localhost:5432/postgres")
        dataSource.user = user
        dataSource.password = password
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
    fun testClearTables() {
        val clearTablesSql = Files.readString(Paths.get("src/main/sql/clearTables.sql"))
        connection.createStatement().use { stmt ->
            stmt.execute(clearTablesSql)
        }

        assertEquals(0, getTableCount("users"))
        assertEquals(0, getTableCount("clubs"))
        assertEquals(0, getTableCount("courts"))
        assertEquals(0, getTableCount("rentals"))
    }
}
