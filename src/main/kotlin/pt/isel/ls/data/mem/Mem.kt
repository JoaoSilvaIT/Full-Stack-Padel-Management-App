package pt.isel.ls.data.mem

import kotlinx.datetime.LocalDate
import pt.isel.ls.data.models.Club
import pt.isel.ls.data.models.Court
import pt.isel.ls.data.models.Email
import pt.isel.ls.data.models.Rental
import pt.isel.ls.data.models.User
import java.util.UUID

object Mem {
    val users =
        mutableListOf<User>(
            User(UUID.randomUUID(), "John Doe", Email("johndoe@example.com"), 1, "123456789"),
            User(UUID.randomUUID(), "John Doe", Email("johndoe@example.com"), 2, "123456789"),
            User(UUID.randomUUID(), "John Doe", Email("johndoe@example.com"), 3, "123456789"),
            User(UUID.randomUUID(), "John Doe", Email("johndoe@example.com"), 4, "123456789"),
            User(UUID.randomUUID(), "John Doe", Email("johndoe@example.com"), 5, "123456789"),
            User(UUID.randomUUID(), "John Doe", Email("johndoe@example.com"), 6, "123456789"),
            User(UUID.randomUUID(), "John Doe", Email("johndoe@example.com"), 7, "123456789"),
            User(UUID.randomUUID(), "John Doe", Email("johndoe@example.com"), 8, "123456789"),
            User(UUID.randomUUID(), "John Doe", Email("johndoe@example.com"), 9, "123456789"),
            User(UUID.randomUUID(), "John Doe", Email("johndoe@example.com"), 10, "123456789"),
            User(UUID.randomUUID(), "John Doe", Email("johndoe@example.com"), 11, "123456789"),
            User(UUID.randomUUID(), "John Doe", Email("johndoe@example.com"), 12, "123456789"),
            User(UUID.randomUUID(), "John Doe", Email("johndoe@example.com"), 13, "123456789"),
            User(UUID.randomUUID(), "John Doe", Email("johndoe@example.com"), 14, "123456789"),
        )

    val clubs =
        mutableListOf<Club>(
            Club(1, "A club", 1),
            Club(2, "Test club", 1),
            Club(3, "Other club", 1),
            Club(4, "Other OTHER club", 1),
            Club(5, "Is this a club?", 1),
            Club(6, "My club", 1),
            Club(7, "his club", 1),
            Club(8, "her club", 1),
            Club(9, "your club", 1),
            Club(10, "our club (comuna)", 1),
            Club(11, "yup", 1),
            Club(12, "A club", 1),
            Club(13, "A club", 1),
        )

    val courts =
        mutableListOf<Court>(
            Court("Court name", 1, 1, 1),
            Court("Court name", 1, 1, 1),
            Court("Court name", 1, 1, 1),
            Court("Court name", 1, 1, 1),
            Court("Court name", 1, 1, 1),
            Court("Court name", 1, 1, 1),
            Court("Court name", 1, 1, 1),
            Court("Court name", 1, 1, 1),
            Court("Court name", 1, 1, 1),
            Court("Court name", 1, 1, 1),
            Court("Court name", 1, 1, 1),
            Court("Court name", 1, 1, 1),
        )

    val rentals =
        mutableListOf<Rental>(
            Rental(1, 1, 1, 1, LocalDate(2025, 4, 30), 2, 2, 1),
            Rental(1, 2, 1, 1, LocalDate(2025, 4, 30), 2, 2, 1),
            Rental(1, 3, 1, 1, LocalDate(2025, 4, 30), 2, 2, 1),
            Rental(1, 4, 1, 1, LocalDate(2025, 4, 30), 2, 2, 1),
            Rental(1, 5, 1, 1, LocalDate(2025, 4, 30), 2, 2, 1),
            Rental(1, 6, 1, 1, LocalDate(2025, 4, 30), 2, 2, 1),
            Rental(1, 7, 1, 1, LocalDate(2025, 4, 30), 2, 2, 1),
            Rental(1, 8, 1, 1, LocalDate(2025, 4, 30), 2, 2, 1),
            Rental(1, 9, 1, 1, LocalDate(2025, 4, 30), 2, 2, 1),
            Rental(1, 10, 1, 1, LocalDate(2025, 4, 30), 2, 2, 1),
            Rental(1, 11, 1, 1, LocalDate(2025, 4, 30), 2, 2, 1),
        )
}
