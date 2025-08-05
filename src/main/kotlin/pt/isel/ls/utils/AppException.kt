package pt.isel.ls.utils

abstract class AppException : Exception() {
    class NotFound(override val message: String? = null) : AppException()

    class Conflict(override val message: String? = null) : AppException()

    class InvalidData(override val message: String? = null) : AppException()

    class Unauthorized(override val message: String? = null) : AppException()
}
