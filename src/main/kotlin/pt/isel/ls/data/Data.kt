package pt.isel.ls.data

interface Data {
    val users: UsersData
    val clubs: ClubsData
    val courts: CourtsData
    val rentals: RentalsData
}
