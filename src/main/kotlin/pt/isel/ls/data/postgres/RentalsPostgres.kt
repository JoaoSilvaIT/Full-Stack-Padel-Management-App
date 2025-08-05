package pt.isel.ls.data.postgres

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import pt.isel.ls.api.models.CourtDraft
import pt.isel.ls.api.models.RentalDetails
import pt.isel.ls.api.models.UserDraft
import pt.isel.ls.data.RentalsData
import pt.isel.ls.data.models.Rental
import pt.isel.ls.data.models.RentalCreate
import pt.isel.ls.utils.AppException
import pt.isel.ls.utils.PaginatedList
import pt.isel.ls.utils.toDate
import pt.isel.ls.utils.useWithRollback
import java.sql.Connection
import java.sql.Statement

class RentalsPostgres(private val conn: () -> Connection) : RentalsData {
    override fun createRental(rentalCreate: RentalCreate): Rental =
        conn().useWithRollback {
            val sql =
                """
                INSERT INTO Rentals (cid, crid, uid, startdate, starthour, duration, ownerid)
                SELECT ?,?,?,?,?,?, Clubs.ownerid
                FROM Clubs
                WHERE Clubs.cid = ?
                """.trimIndent()

            val statement =
                it.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS,
                ).apply {
                    setInt(1, rentalCreate.club)
                    setInt(2, rentalCreate.court)
                    setInt(3, rentalCreate.uid)
                    setDate(4, rentalCreate.date.toDate())
                    setInt(5, rentalCreate.startHour)
                    setInt(6, rentalCreate.duration)
                    setInt(7, rentalCreate.club)
                }
            if (statement.executeUpdate() == 0) throw AppException.InvalidData("Rental creation failed")
            val generated = statement.generatedKeys
            if (generated.next()) {
                return Rental(
                    generated.getInt("rid"),
                    generated.getInt("ownerid"),
                    rentalCreate.club,
                    rentalCreate.court,
                    LocalDate.parse(generated.getDate("startdate").toString()),
                    rentalCreate.startHour,
                    rentalCreate.duration,
                    rentalCreate.uid,
                )
            } else {
                throw AppException.InvalidData("Rental creation failed")
            }
        }

    override fun getRentalDetails(
        cid: Int,
        crid: Int,
        rid: Int,
    ): RentalDetails =
        conn().useWithRollback {
            val sql =
                """
                SELECT 
                    r.rid,
                    r.startdate,
                    r.starthour,
                    r.duration,
                    
                    u.uid as renter_id,
                    u.name as renter_name,
                    
                    cr.crid AS court_id,
                    cr.cid AS court_cid,
                    cr.name AS court_name
                FROM Rentals r
                JOIN Users u ON u.uid = r.uid
                LEFT JOIN Courts cr
                    ON cr.crid = r.crid
                    AND cr.cid = r.cid
                    AND cr.ownerid = r.ownerid
                WHERE
                    r.rid = ?
                    AND r.crid = ?
                    AND r.cid = ?
                    
                """.trimIndent()

            it.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, rid)
                stmt.setInt(2, crid)
                stmt.setInt(3, cid)

                stmt.executeQuery().use { rs ->

                    if (rs.next()) {
                        val renter =
                            UserDraft(
                                rs.getInt("renter_id"),
                                rs.getString("renter_name"),
                            )

                        val court =
                            CourtDraft(
                                rs.getInt("court_cid"),
                                rs.getInt("court_id"),
                                rs.getString("court_name"),
                            )

                        return RentalDetails(
                            rs.getInt("rid"),
                            renter,
                            court,
                            LocalDate.parse(rs.getDate("startdate").toString()),
                            rs.getInt("starthour"),
                            rs.getInt("duration"),
                        )
                    }

                    throw AppException.NotFound("Rental Not Found")
                }
            }
        }

    override fun getRental(
        cid: Int,
        crid: Int,
        rid: Int,
    ): Rental =
        conn().useWithRollback {
            val sql =
                """
                SELECT *
                FROM Rentals
                WHERE
                    rid = ?
                    AND crid = ?
                    AND cid = ?
                """.trimIndent()

            val statement =
                it.prepareStatement(sql).apply {
                    setInt(1, rid)
                    setInt(2, crid)
                    setInt(3, cid)
                }
            val rental = statement.executeQuery()
            if (rental.next()) {
                return Rental(
                    rental.getInt("ownerid"),
                    rental.getInt("rid"),
                    rental.getInt("cid"),
                    rental.getInt("crid"),
                    LocalDate.parse(rental.getDate("startdate").toString()),
                    rental.getInt("starthour"),
                    rental.getInt("duration"),
                    rental.getInt("uid"),
                )
            } else {
                throw AppException.NotFound("Rental Not Found")
            }
        }

    override fun getRentals(
        cid: Int,
        crid: Int,
        date: LocalDate?,
        skip: Int,
        limit: Int,
    ): PaginatedList<Rental> =
        conn().useWithRollback {
            val sql =
                """
                SELECT *
                FROM Rentals
                WHERE cid = ? AND crid = ? ${if (date != null) " AND startdate = ? " else ""}
                LIMIT ? OFFSET ?
                """.trimIndent()

            it.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, cid)
                stmt.setInt(2, crid)
                date?.let { stmt.setDate(3, date.toDate()) }
                stmt.setInt(if (date == null) 3 else 4, limit + 1)
                stmt.setInt(if (date == null) 4 else 5, skip)

                val rs = stmt.executeQuery()
                val rentals = mutableListOf<Rental>()
                while (rs.next()) {
                    rentals.add(
                        Rental(
                            rs.getInt("ownerId"),
                            rs.getInt("rid"),
                            rs.getInt("cid"),
                            rs.getInt("crid"),
                            LocalDate.parse(rs.getDate("startdate").toString()),
                            rs.getInt("starthour"),
                            rs.getInt("duration"),
                            rs.getInt("uid"),
                        ),
                    )
                }

                return PaginatedList.fromList(rentals, skip, limit)
            }
        }

    override fun getRentalsByUser(
        uid: Int,
        skip: Int,
        limit: Int,
    ): PaginatedList<Rental> {
        conn().useWithRollback {
            val sql =
                """
                SELECT *
                FROM Rentals
                WHERE uid = ?
                LIMIT ? OFFSET ?
                """.trimIndent()

            it.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, uid)
                stmt.setInt(2, limit + 1)
                stmt.setInt(3, skip)

                val rs = stmt.executeQuery()
                val rentals = mutableListOf<Rental>()
                while (rs.next()) {
                    rentals.add(
                        Rental(
                            rs.getInt("ownerId"),
                            rs.getInt("rid"),
                            rs.getInt("cid"),
                            rs.getInt("crid"),
                            rs.getDate("startdate").toLocalDate().toKotlinLocalDate(),
                            rs.getInt("starthour"),
                            rs.getInt("duration"),
                            rs.getInt("uid"),
                        ),
                    )
                }
                println(rentals)
                return PaginatedList.fromList(rentals, skip, limit)
            }
        }
    }

    override fun getAvailableRentHours(
        cid: Int,
        crid: Int,
        date: LocalDate,
    ): Array<Int> =
        conn().useWithRollback {
            val statement =
                it.prepareStatement("""SELECT * FROM Rentals WHERE cid = ? AND crid = ?  AND startdate = ? """.trimIndent())
                    .apply {
                        setInt(1, cid)
                        setInt(2, crid)
                        setDate(3, date.toDate())
                    }
            val rentals = statement.executeQuery()
            val availablehours = Array<Int>(24) { it }
            while (rentals.next()) {
                val shour = rentals.getInt("starthour")
                val dur = rentals.getInt("duration")
                for (hour in shour..<shour + dur) {
                    availablehours[hour] = -1
                }
            }
            return availablehours.filter { it != -1 }.toTypedArray()
        }

    override fun deleteRental(rid: Int) =
        conn().useWithRollback {
            val statement =
                it.prepareStatement("""DELETE FROM Rentals WHERE rid = ?""".trimIndent()).apply {
                    setInt(1, rid)
                }
            if (statement.executeUpdate() == 0) throw AppException.NotFound("Rental Not Found")
        }

    override fun updateRental(
        rid: Int,
        date: LocalDate,
        startHour: Int,
        duration: Int,
    ): Rental =
        conn().useWithRollback {
            val sql =
                """
                UPDATE Rentals
                SET startdate = ?, starthour = ? ,duration = ?
                WHERE rid = ?
                RETURNING *
                """.trimIndent()

            it.prepareStatement(sql).use { stmt ->
                stmt.setDate(1, date.toDate())
                stmt.setInt(2, startHour)
                stmt.setInt(3, duration)
                stmt.setInt(4, rid)

                val rs = stmt.executeQuery()
                if (rs.next()) {
                    return Rental(
                        rs.getInt("ownerId"),
                        rs.getInt("rid"),
                        rs.getInt("cid"),
                        rs.getInt("crid"),
                        LocalDate.parse(rs.getDate("startdate").toString()),
                        rs.getInt("starthour"),
                        rs.getInt("duration"),
                        rs.getInt("uid"),
                    )
                } else {
                    throw AppException.NotFound("Rental Not Found")
                }
            }
        }
}
