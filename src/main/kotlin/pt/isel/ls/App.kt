package pt.isel.ls

import org.http4k.server.Jetty
import org.slf4j.LoggerFactory
import pt.isel.ls.api.API
import pt.isel.ls.api.Middleware
import pt.isel.ls.data.mem.DataMem
import pt.isel.ls.data.postgres.DataPostgres
import pt.isel.ls.services.Services
import pt.isel.ls.utils.createServer

private val logger = LoggerFactory.getLogger("pt.isel.ls.App")

fun main() {
    val env = System.getenv()

    val postgres =
        (env["DB_ACTIVE"]?.lowercase() == "true" && env["DB_USER"] != null && env["DB_PASS"] != null) || env["JDBC_DATABASE_URL"] != null

    val data = if (postgres) DataPostgres else DataMem
    val services = Services(data)
    val middleware = Middleware(services)
    val api = API(services, middleware)

    val port = env["PORT"]?.toInt() ?: 8080

    createServer(Jetty(port)) {
        static("./static-content/spa")
        middleware(middleware::exceptionHandler) {
            route("users") {
                post("login", api.users::loginUser)
                post(handler = api.users::createUser)
                get(handler = api.users::getUsers)
                get("{uid}", api.users::getUser)
                get("{uid}/rentals", api.rentals::getRentalsByUser)
            }

            route("clubs") {
                get(handler = api.clubs::getClubs) // all
                middleware(middleware::userTokenConverter) {
                    post(handler = api.clubs::createClub) // create
                }
                route("{cid}") {
                    get(handler = api.clubs::getClubDetails)

                    middleware(middleware::userTokenConverter) {
                        put(handler = api.clubs::updateClubById)
                    }
                    route("courts") {
                        get(handler = api.courts::getCourtsByClubID)

                        middleware(middleware::userTokenConverter) {
                            post(handler = api.courts::createCourt)
                        }

                        route("{crid}") {
                            get(handler = api.courts::getCourtDetails)
                            get("available_hours", api.rentals::getAvailableRentHours)

                            route("rentals") {
                                get(handler = api.rentals::getRentals) // with date, skip, limit query params

                                middleware(middleware::userTokenConverter) {
                                    post(handler = api.rentals::createRental)
                                }

                                route("{rid}") {
                                    get(handler = api.rentals::getRentalDetails)
                                    middleware(middleware::userTokenConverter) {
                                        put(handler = api.rentals::updateRental)
                                        delete(handler = api.rentals::deleteRental)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }.let { server ->
        logger.info("Server started listening at http://localhost:${server.port()}!")
        // readln()

        // server.stop()
        // logger.info("Server closed. Exiting")
    }
}
