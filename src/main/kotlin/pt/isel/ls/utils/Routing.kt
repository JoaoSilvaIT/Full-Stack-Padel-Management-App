package pt.isel.ls.utils

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.then
import org.http4k.routing.ResourceLoader
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.ServerConfig
import org.http4k.server.asServer

open class Route(
    val path: String,
) : Routing() {
    private val methods = mutableListOf<RoutingHttpHandler>()

    fun get(
        subpath: String = "",
        handler: (Request) -> Response,
    ) {
        methods.add(subpath bind Method.GET to handler)
    }

    fun post(
        subpath: String = "",
        handler: (Request) -> Response,
    ) {
        methods.add(subpath bind Method.POST to handler)
    }

    fun put(
        subpath: String = "",
        handler: (Request) -> Response,
    ) {
        methods.add(subpath bind Method.PUT to handler)
    }

    fun delete(
        subpath: String = "",
        handler: (Request) -> Response,
    ) {
        methods.add(subpath bind Method.DELETE to handler)
    }

    fun middleware(
        handler: (Request, HttpHandler) -> Response,
        configuration: Route.() -> Unit,
    ) {
        val filter =
            Filter { next: HttpHandler ->
                { request: Request ->
                    handler(request, next)
                }
            }
        val route = Route("")
        route.configuration()
        methods.add(filter.then(routes(route.getRoutes())))
    }

    override fun getRoutes(): List<RoutingHttpHandler> = super.getRoutes() + methods
}

class Middleware(
    val filter: Filter,
) : Routing() {
    private val routes = mutableListOf<Route>()

    override fun route(
        path: String,
        configuration: Route.() -> Unit,
    ) {
        val route = Route(path)
        route.configuration()
        routes.add(route)
    }

    override fun getRoutes(): List<RoutingHttpHandler> =
        routes.mapNotNull {
            val routes = it.getRoutes()
            if (routes.isEmpty()) {
                null
            } else {
                filter.then(it.path bind routes(routes))
            }
        }
}

open class Routing {
    private val routes = mutableListOf<Route>()

    open fun route(
        path: String,
        configuration: Route.() -> Unit,
    ) {
        val route = Route(path)
        route.configuration()
        routes.add(route)
    }

    open fun getRoutes(): List<RoutingHttpHandler> =
        routes.mapNotNull {
            val routes = it.getRoutes()
            if (routes.isEmpty()) {
                null
            } else {
                it.path bind routes(routes)
            }
        }
}

open class Application {
    private var routings = mutableListOf<Routing>()
    private var middlewares = mutableListOf<Middleware>()
    private var statics = mutableListOf<RoutingHttpHandler>()

    fun routing(configuration: Routing.() -> Unit) {
        val routing = Routing()
        routing.configuration()
        routings.add(routing)
    }

    fun middleware(
        handler: (Request, HttpHandler) -> Response,
        configuration: Middleware.() -> Unit,
    ) {
        val filter =
            Filter { next: HttpHandler ->
                { request: Request ->
                    handler(request, next)
                }
            }

        val middleware = Middleware(filter)
        middleware.configuration()
        middlewares.add(middleware)
    }

    fun static(
        directory: String = ".",
        path: String = "",
    ) = statics.add(
        path bind org.http4k.routing.static(ResourceLoader.Directory(directory)),
    )

    fun getRoute(): RoutingHttpHandler {
        val routings =
            routings.mapNotNull {
                val routes = it.getRoutes()
                if (routes.isEmpty()) {
                    null
                } else {
                    routes(routes)
                }
            }

        val middles =
            middlewares.mapNotNull {
                val routes = it.getRoutes()
                if (routes.isEmpty()) {
                    null
                } else {
                    routes(routes)
                }
            }

        return routes(middles + routings + statics)
    }
}

fun createServer(
    server: ServerConfig,
    configuration: Application.() -> Unit,
): Http4kServer {
    val app = Application()
    app.configuration()
    println(app.getRoute())
    return app.getRoute().asServer(server).start()
}
