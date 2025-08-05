package pt.isel.ls.data.postgres

import org.postgresql.util.PSQLException
import pt.isel.ls.api.models.ClubDraft
import pt.isel.ls.api.models.RentalDraft
import pt.isel.ls.api.models.UserDetails
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.models.Email
import pt.isel.ls.data.models.User
import pt.isel.ls.data.models.UserCreate
import pt.isel.ls.utils.AppException
import pt.isel.ls.utils.PaginatedList
import pt.isel.ls.utils.Password
import pt.isel.ls.utils.useWithRollback
import java.sql.Connection
import java.sql.Statement
import java.util.UUID

class UsersPostgres(
    private val conn: () -> Connection,
) : UsersData {
    override fun createUser(userCreate: UserCreate): User =
        try {
            conn().useWithRollback {
                val statement =
                    it.prepareStatement(
                        """INSERT INTO Users (email, name,password) VALUES (?,?,?) """.trimIndent(),
                        Statement.RETURN_GENERATED_KEYS,
                    ).apply {
                        setString(1, userCreate.mail)
                        setString(2, userCreate.name)
                        setString(3, Password(userCreate.password).hash())
                    }
                if (statement.executeUpdate() == 0) {
                    throw AppException.InvalidData("Failed user creation")
                }
                val generatedKeys = statement.generatedKeys

                if (generatedKeys.next()) {
                    return User(
                        generatedKeys.getObject(2) as UUID,
                        userCreate.name,
                        Email(userCreate.mail),
                        generatedKeys.getInt(1),
                        userCreate.password,
                    )
                }
                throw AppException.InvalidData("Failed user creation")
            }
        } catch (e: PSQLException) {
            if (e.sqlState == "23505") {
                throw AppException.Conflict("A user with this email already exists.")
            }
            throw AppException.InvalidData("Database error: ${e.message}")
        }

    override fun getUserDetails(uid: Int): UserDetails =
        conn().useWithRollback {
            val sql =
                """
                SELECT
                    u.uid,
                    u.name AS user_name,
                    cl.cid AS club_id,
                    cl.name AS club_name,
                    r.rid AS rental_id,
                    r.uid AS rental_uid
                FROM Users u
                LEFT JOIN Clubs cl 
                    ON cl.ownerid = u.uid
                LEFT JOIN Rentals r 
                    ON r.uid = u.uid
                    AND r.cid = cl.cid
                    AND r.ownerid = cl.ownerid
                WHERE u.uid = ?;
                """.trimIndent()

            it.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, uid)

                stmt.executeQuery().use { rs ->
                    var userName: String? = null
                    val clubs = mutableSetOf<ClubDraft>()
                    val rentals = mutableSetOf<RentalDraft>()

                    while (rs.next()) {
                        if (userName == null) {
                            userName = rs.getString("user_name")
                        }

                        val clubId = rs.getInt("club_id")
                        if (!rs.wasNull()) {
                            val clubName = rs.getString("club_name")
                            clubs += ClubDraft(clubId, clubName)
                        }

                        val rentalId = rs.getInt("rental_id")
                        if (!rs.wasNull()) {
                            val rentalUserId = rs.getInt("rental_uid")
                            rentals += RentalDraft(rentalId, rentalUserId)
                        }
                    }

                    if (userName != null) {
                        return UserDetails(uid, userName, clubs.toList(), rentals.toList())
                    }
                    throw AppException.NotFound("User Not Found")
                }
            }
        }

    override fun getUser(uid: Int): User =
        conn().useWithRollback {
            val statement =
                it.prepareStatement("""SELECT * FROM Users WHERE uid = ?""".trimIndent()).apply {
                    setInt(1, uid)
                }
            val user = statement.executeQuery()
            if (user.next()) {
                return User(
                    user.getObject("token") as UUID,
                    user.getString("name"),
                    Email(user.getString("email")),
                    user.getInt("uid"),
                    user.getString("password"),
                )
            }
            throw AppException.NotFound("User Not Found")
        }

    override fun getIdByToken(token: UUID): Int {
        conn().useWithRollback {
            val stmt =
                it.prepareStatement("SELECT uid FROM Users WHERE token = ?").apply {
                    setObject(1, token)
                }
            val user = stmt.executeQuery()
            if (user.next()) {
                return user.getObject("uid") as Int
            }
        }
        throw AppException.NotFound("User Not Found")
    }

    override fun getUsers(
        skip: Int,
        limit: Int,
    ): PaginatedList<User> {
        conn().useWithRollback {
            val sql =
                """
                SELECT *
                FROM Users
                LIMIT ? OFFSET ?
                """.trimIndent()

            it.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, limit + 1)
                stmt.setInt(2, skip)

                val rs = stmt.executeQuery()
                val users = mutableListOf<User>()
                while (rs.next()) {
                    users.add(
                        User(
                            rs.getObject("token") as UUID,
                            rs.getString("name"),
                            Email(rs.getString("email")),
                            rs.getInt("uid"),
                            rs.getString("password"),
                        ),
                    )
                }

                return PaginatedList.fromList(users, skip, limit)
            }
        }
    }

    override fun getUserByEmail(email: String): User =
        conn().useWithRollback {
            val statement =
                it.prepareStatement("""SELECT * FROM Users WHERE email = ?""".trimIndent()).apply {
                    setString(1, email)
                }
            val user = statement.executeQuery()
            if (user.next()) {
                return User(
                    user.getObject("token") as UUID,
                    user.getString("name"),
                    Email(user.getString("email")),
                    user.getInt("uid"),
                    user.getString("password"),
                )
            }
            throw AppException.NotFound("User Not Found")
        }
}
