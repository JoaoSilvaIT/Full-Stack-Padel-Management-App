package pt.isel.ls.utils

import kotlinx.serialization.Serializable
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

@Serializable
class Password(val value: String) {
    init {
        if (!strongPasswordRegex.matches(value)) {
            throw AppException.InvalidData(
                "Password must be at lest $MIN_PASSWORD_SIZE characters long and not contain invalid characters.",
            )
        }
    }

    fun hash(): String {
        val salt = ByteArray(SALT_LENGTH).also { RNG.nextBytes(it) }
        val digest = MessageDigest.getInstance(ALGORITHM)
        digest.update(salt)
        digest.update(value.toByteArray())
        val hashBytes = digest.digest()
        val encoder = Base64.getEncoder()
        return listOf(ALGORITHM, encoder.encodeToString(salt), encoder.encodeToString(hashBytes)).joinToString(":")
    }

    fun verify(stored: String): Boolean {
        val parts = stored.split(":")
        require(parts.size == 3) { "Hash format inválido." }
        val (algo, saltB64, hashB64) = parts
        require(algo == ALGORITHM) { "Algoritmo incompatível: $algo" }
        val salt = Base64.getDecoder().decode(saltB64)
        val expectedHash = Base64.getDecoder().decode(hashB64)

        val digest = MessageDigest.getInstance(ALGORITHM)
        digest.update(salt)
        digest.update(value.toByteArray())
        val actualHash = digest.digest()
        return MessageDigest.isEqual(actualHash, expectedHash)
    }

    companion object {
        private const val MIN_PASSWORD_SIZE = 4
        val strongPasswordRegex = Regex("""^[a-zA-Z0-9!@#$%^&*()_\-+=\[{\]};:'",<.>/?\\|`~]{$MIN_PASSWORD_SIZE,}$""")
        private const val ALGORITHM = "SHA-256"
        private const val SALT_LENGTH = 16 // bytes
        private val RNG = SecureRandom()
    }
}
