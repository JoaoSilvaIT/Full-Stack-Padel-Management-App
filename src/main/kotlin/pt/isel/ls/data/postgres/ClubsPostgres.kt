package pt.isel.ls.data.postgres

import org.postgresql.util.PSQLException
import pt.isel.ls.api.models.ClubDetails
import pt.isel.ls.api.models.CourtDraft
import pt.isel.ls.api.models.UserDraft
import pt.isel.ls.data.ClubsData
import pt.isel.ls.data.models.Club
import pt.isel.ls.data.models.ClubCreate
import pt.isel.ls.data.models.ClubUpdate
import pt.isel.ls.utils.AppException
import pt.isel.ls.utils.PaginatedList
import pt.isel.ls.utils.useWithRollback
import java.sql.Connection
import java.sql.Statement

class ClubsPostgres(private val conn: () -> Connection) : ClubsData {
    override fun createClub(clubCreate: ClubCreate): Club =
        try {
            conn().useWithRollback {
                val statement =
                    it.prepareStatement(
                        """INSERT INTO Clubs(name, ownerid) VALUES (?,?)""".trimIndent(),
                        Statement.RETURN_GENERATED_KEYS,
                    ).apply {
                        setString(1, clubCreate.name)
                        setInt(2, clubCreate.ownerId)
                    }
                if (statement.executeUpdate() == 0) throw AppException.InvalidData("Club creation failed")

                val generatedKeys = statement.generatedKeys
                if (generatedKeys.next()) {
                    return Club(generatedKeys.getInt(1), clubCreate.name, clubCreate.ownerId)
                } else {
                    throw AppException.InvalidData("Club creation failed")
                }
            }
        } catch (e: PSQLException) {
            if (e.sqlState == "23505") {
                throw AppException.Conflict("A club with this name already exists.")
            }
            throw AppException.InvalidData("Database error: ${e.message}")
        }

    override fun updateClub(
        cid: Int,
        clubUpdate: ClubUpdate,
    ): Club {
        conn().useWithRollback {
            val sql =
                """
                UPDATE Clubs
                SET name = ?
                WHERE cid = ?
                RETURNING *
                """.trimIndent()

            it.prepareStatement(sql).use { stmt ->
                stmt.setString(1, clubUpdate.name)
                stmt.setInt(2, cid)

                val rs = stmt.executeQuery()
                if (rs.next()) {
                    return Club(cid, clubUpdate.name, rs.getInt("ownerid"))
                }
                throw AppException.InvalidData("Club update failed")
            }
        }
    }

    override fun getClubDetails(cid: Int): ClubDetails =
        conn().useWithRollback {
            val sql =
                """
                SELECT 
                    c.cid,
                    c.name AS club_name,
                    u.uid AS owner_id,
                    u.name AS owner_name,
                    cr.crid AS court_id,
                    cr.name AS court_name
                FROM Clubs c
                JOIN Users u ON c.ownerid = u.uid
                LEFT JOIN Courts cr ON cr.cid = c.cid AND cr.ownerid = c.ownerid
                WHERE c.cid = ?
                """.trimIndent()

            it.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, cid)

                stmt.executeQuery().use { rs ->
                    var cid: Int? = null
                    var clubName: String? = null
                    var owner: UserDraft? = null
                    val courts = mutableListOf<CourtDraft>()

                    while (rs.next()) {
                        if (cid == null) {
                            cid = rs.getInt("cid")
                            clubName = rs.getString("club_name")

                            owner =
                                UserDraft(
                                    rs.getInt("owner_id"), rs.getString("owner_name"),
                                )
                        }

                        val crid = rs.getInt("court_id")
                        if (!rs.wasNull()) {
                            courts.add(
                                CourtDraft(
                                    rs.getInt("cid"),
                                    crid,
                                    rs.getString("court_name"),
                                ),
                            )
                        }
                    }

                    if (cid != null && clubName != null && owner != null) {
                        return ClubDetails(cid, clubName, owner, courts)
                    }
                    throw AppException.NotFound("Club Not Found")
                }
            }
        }

    override fun getClub(cid: Int): Club =
        conn().useWithRollback {
            val sql =
                """
                SELECT *
                FROM Clubs
                WHERE
                    cid = ?
                """.trimIndent()

            it.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, cid)

                stmt.executeQuery().use { club ->
                    if (club.next()) {
                        return Club(club.getInt("cid"), club.getString("name"), club.getInt("ownerid"))
                    }
                    throw AppException.NotFound("Club Not Found")
                }
            }
        }

    override fun getClubDetailsByName(name: String): Club {
        TODO("Not yet implemented")
    }

    override fun getClubs(
        skip: Int,
        limit: Int,
        partialName: String,
    ): PaginatedList<Club> {
        conn().useWithRollback {
            val sql =
                """
                SELECT *
                FROM Clubs
                WHERE Clubs.name ILIKE ?
                ${""}
                LIMIT ? OFFSET ?
                """.trimIndent()

            it.prepareStatement(sql).use { stmt ->
                stmt.setString(1, "%$partialName%")
                stmt.setInt(2, limit + 1)
                stmt.setInt(3, skip)
                val rs = stmt.executeQuery()

                val clubs = mutableListOf<Club>()
                while (rs.next()) {
                    clubs.add(Club(rs.getInt("cid"), rs.getString("name"), rs.getInt("ownerid")))
                }

                return PaginatedList.fromList(clubs, skip, limit)
            }
        }
    }
}
