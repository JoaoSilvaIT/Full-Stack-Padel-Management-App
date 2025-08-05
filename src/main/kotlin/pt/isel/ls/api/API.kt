package pt.isel.ls.api

import pt.isel.ls.services.Services

class API(services: Services, middleware: Middleware) {
    val users = UsersAPI(services)
    val clubs = ClubsAPI(services, middleware.userIdKey)
    val courts = CourtsAPI(services, middleware.userIdKey)
    val rentals = RentalsAPI(services, middleware.userIdKey)
}
