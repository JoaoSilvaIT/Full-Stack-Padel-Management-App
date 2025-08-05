package pt.isel.ls.sql

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.postgresql.ds.PGSimpleDataSource
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import kotlin.test.assertTrue

class CreateSchemaTest {
    private lateinit var connection: Connection

    @Before
    fun setup() {
        connection = createDatabaseConnection()
        "src/main/sql/createSchema.sql".executeScript()
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

    private fun String.executeScript() {
        val scriptContent = Files.readString(Paths.get(this))
        connection.createStatement().use { stmt ->
            stmt.execute(scriptContent)
        }
    }

    private fun dropSchema() {
        connection.createStatement().use { stmt ->
            stmt.execute("DROP SCHEMA public CASCADE; CREATE SCHEMA public;")
        }
    }

    private fun getTableColumns(tableName: String): Set<String> {
        connection.createStatement().use { stmt ->
            val rs = stmt.executeQuery("SELECT column_name FROM information_schema.columns WHERE table_name = '$tableName'")
            val columns = mutableSetOf<String>()
            while (rs.next()) {
                columns.add(rs.getString("column_name"))
            }
            return columns
        }
    }

    private fun getAllTables(): Set<String> {
        connection.createStatement().use { stmt ->
            val rs = stmt.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'")
            val tables = mutableSetOf<String>()
            while (rs.next()) {
                tables.add(rs.getString("table_name"))
            }
            return tables
        }
    }

    @Test
    fun testTablesCreated() {
        val tables = getAllTables()
        assertTrue(tables.containsAll(listOf("users", "clubs", "courts", "rentals")))
    }

    @Test
    fun testUsersTableColumns() {
        val columns = getTableColumns("users")
        assertTrue(columns.containsAll(listOf("uid", "email", "name")))
    }

    @Test
    fun testClubsTableColumns() {
        val columns = getTableColumns("clubs")
        assertTrue(columns.containsAll(listOf("cid", "name")))
    }

    @Test
    fun testCourtsTableColumns() {
        val columns = getTableColumns("courts")
        assertTrue(columns.containsAll(listOf("crid", "name", "cid", "ownerid")))
    }

    @Test
    fun testRentalsTableColumns() {
        val columns = getTableColumns("rentals")
        assertTrue(columns.containsAll(listOf("rid", "uid", "cid", "crid", "ownerid", "startdate", "starthour", "duration")))
    }
}
