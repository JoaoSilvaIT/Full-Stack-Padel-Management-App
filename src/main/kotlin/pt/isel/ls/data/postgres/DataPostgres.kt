package pt.isel.ls.data.postgres

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.data.ClubsData
import pt.isel.ls.data.CourtsData
import pt.isel.ls.data.Data
import pt.isel.ls.data.RentalsData
import pt.isel.ls.data.UsersData
import java.io.File

object DataPostgres : Data {
    private val dataSource =
        PGSimpleDataSource().apply {
            val env = System.getenv()
            val remoteUrl = env["JDBC_DATABASE_URL"]
            if (remoteUrl != null) {
                setUrl(remoteUrl)
            } else {
                user = env["DB_USER"] ?: error("Environment variable DB_USER not set")
                password = env["DB_PASS"] ?: error("Environment variable DB_PASS not set")
                setURL("jdbc:postgresql://localhost:5432/postgres")
            }
        }

    private fun conn() = dataSource.connection.also { it.autoCommit = false }

    fun create() {
        val script = File("src/main/sql/createSchema.sql").readText()
        conn().prepareStatement(script).executeUpdate()
    }

    fun delete() {
        val script = File("src/main/sql/deleteSchema.sql").readText()
        conn().prepareStatement(script).executeUpdate()
    }

    fun reset() {
        val script = File("src/main/sql/resetSchema.sql").readText()
        conn().prepareStatement(script).executeUpdate()
    }

    override val users: UsersData = UsersPostgres(::conn)
    override val clubs: ClubsData = ClubsPostgres(::conn)
    override val courts: CourtsData = CourtsPostgres(::conn)
    override val rentals: RentalsData = RentalsPostgres(::conn)
}
