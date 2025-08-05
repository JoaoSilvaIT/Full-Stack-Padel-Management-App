package pt.isel.ls.sql

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.postgresql.ds.PGSimpleDataSource
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import kotlin.test.assertFalse

class DeleteSchemaTest {
    private lateinit var connection: Connection

    @Before
    fun setup() {
        connection = createDatabaseConnection()
        executeScript("src/main/sql/createSchema.sql")
        executeScript("src/main/sql/addData.sql")
    }

    @After
    fun teardown() {
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

    private fun tableExists(tableName: String): Boolean {
        connection.createStatement().use { stmt ->
            val rs =
                stmt.executeQuery(
                    "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_name = '$tableName'",
                )
            return rs.next()
        }
    }

    @Test
    fun testDeleteSchema() {
        executeScript("src/main/sql/deleteSchema.sql")

        assertFalse(tableExists("users"))
        assertFalse(tableExists("clubs"))
        assertFalse(tableExists("courts"))
        assertFalse(tableExists("rentals"))
    }
}
