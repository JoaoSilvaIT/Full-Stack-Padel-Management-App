package pt.isel.ls.data.postgres

import pt.isel.ls.api.models.ClubResponse
import pt.isel.ls.api.models.CourtDetails
import pt.isel.ls.api.models.RentalDraft
import pt.isel.ls.data.CourtsData
import pt.isel.ls.data.models.Court
import pt.isel.ls.data.models.CourtCreate
import pt.isel.ls.utils.AppException
import pt.isel.ls.utils.PaginatedList
import pt.isel.ls.utils.useWithRollback
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement

class CourtsPostgres(private val conn: () -> Connection) : CourtsData {
    override fun createCourt(courtCreate: CourtCreate): Court =
        try {
            conn().useWithRollback {
                val statement =
                    it.prepareStatement(
                        """INSERT INTO Courts(cid, name, ownerid) VALUES (?,?,?)""".trimIndent(),
                        Statement.RETURN_GENERATED_KEYS,
                    ).apply {
                        setInt(1, courtCreate.club)
                        setString(2, courtCreate.name)
                        setInt(3, courtCreate.uid)
                    }
                if (statement.executeUpdate() == 0) throw AppException.InvalidData("Court creation failed")
                val generatedKeys = statement.generatedKeys
                if (generatedKeys.next()) {
                    return Court(courtCreate.name, courtCreate.club, generatedKeys.getInt(1), courtCreate.uid)
                } else {
                    throw AppException.InvalidData("Court creation failed")
                }
            }
        } catch (_: SQLException) {
            throw AppException.InvalidData("Invalid court data or club does not exist")
        }

    override fun getCourtDetails(
        cid: Int,
        crid: Int,
    ): CourtDetails =
        conn().useWithRollback {
            val sql =
                """
                SELECT 
                    c.crid,
                    c.name AS court_name,
                    cl.cid,
                    cl.name AS club_name,
                    cl.ownerid AS club_owner_id,
                    r.rid,
                    r.uid AS rental_user_id
                FROM Courts c
                JOIN Clubs cl ON c.cid = cl.cid AND c.ownerid = cl.ownerid
                LEFT JOIN Rentals r ON r.crid = c.crid AND r.cid = c.cid AND r.ownerid = c.ownerid
                WHERE c.crid = ? AND c.cid = ?
                """.trimIndent()

            it.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, crid)
                stmt.setInt(2, cid)

                stmt.executeQuery().use { rs ->
                    var courtName: String? = null
                    var club: ClubResponse? = null
                    val rentals = mutableListOf<RentalDraft>()

                    while (rs.next()) {
                        if (courtName == null) {
                            courtName = rs.getString("court_name")
                            val clubId = rs.getInt("cid")
                            val clubName = rs.getString("club_name")
                            val clubOwnerId = rs.getInt("club_owner_id")
                            club = ClubResponse(clubId, clubName, clubOwnerId)
                        }

                        val rentalId = rs.getInt("rid")
                        if (!rs.wasNull()) {
                            val rentalUserId = rs.getInt("rental_user_id")
                            rentals += RentalDraft(rentalId, rentalUserId)
                        }
                    }

                    if (courtName != null && club != null) {
                        return CourtDetails(crid, courtName, club, rentals)
                    }
                    throw AppException.NotFound("Court not found")
                }
            }
        }

    override fun getCourt(
        cid: Int,
        crid: Int,
    ): Court =
        try {
            conn().useWithRollback {
                val statement =
                    it.prepareStatement("""SELECT * FROM Courts WHERE crid = ? AND cid = ?""".trimIndent()).apply {
                        setInt(1, crid)
                        setInt(2, cid)
                    }
                val court = statement.executeQuery()
                if (court.next()) {
                    return Court(
                        court.getString("name"),
                        court.getInt("cid"),
                        court.getInt("crid"),
                        court.getInt("ownerid"),
                    )
                }
                throw AppException.NotFound("Court not found")
            }
        } catch (_: SQLException) {
            throw AppException.InvalidData("Invalid court id")
        }

    override fun getCourtsByClubID(
        cid: Int,
        skip: Int,
        limit: Int,
    ): PaginatedList<Court> {
        try {
            conn().useWithRollback {
                val sql =
                    """
                    SELECT *
                    FROM Courts
                    WHERE cid = ?
                    LIMIT ? OFFSET ?
                    """.trimIndent()

                it.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, cid)
                    stmt.setInt(2, limit + 1)
                    stmt.setInt(3, skip)

                    val rs = stmt.executeQuery()
                    val courts = mutableListOf<Court>()

                    while (rs.next()) {
                        courts.add(
                            Court(
                                rs.getString("name"),
                                rs.getInt("cid"),
                                rs.getInt("crid"),
                                rs.getInt("ownerid"),
                            ),
                        )
                    }

                    return PaginatedList.fromList(courts, skip, limit)
                }
            }
        } catch (_: SQLException) {
            throw AppException.InvalidData("Invalid club id")
        }
    }
}
