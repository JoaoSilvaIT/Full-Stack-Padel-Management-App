package pt.isel.ls.services

import pt.isel.ls.data.Data

class Services(db: Data) {
    val users = UsersServices(db.users)
    val clubs = ClubsServices(db.clubs)
    val courts = CourtsServices(db.courts)
    val rentals = RentalsServices(db.rentals)
}
