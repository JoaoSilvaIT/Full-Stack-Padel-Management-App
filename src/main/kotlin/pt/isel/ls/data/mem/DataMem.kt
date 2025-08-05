package pt.isel.ls.data.mem

import pt.isel.ls.data.Data

object DataMem : Data {
    override val users = UsersDataMem
    override val clubs = ClubsDataMem
    override val courts = CourtsDataMem
    override val rentals = RentalsDataMem
}
