package pt.isel.ls.data.models
import kotlinx.serialization.Serializable
import pt.isel.ls.utils.AppException

@Serializable
data class Email(
    val value: String,
) {
    init {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (!value.matches(emailRegex)) throw AppException.InvalidData()
    }
}
