package pt.isel.ls.utils

import kotlinx.datetime.toKotlinLocalDate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.http4k.core.Request
import org.http4k.routing.path
import java.sql.Connection
import java.sql.Date
import java.time.LocalDate
import java.util.UUID

fun currentLocalDate(): kotlinx.datetime.LocalDate = LocalDate.now().toKotlinLocalDate()

fun kotlinx.datetime.LocalDate.isPast() = this < currentLocalDate()

fun Request.paramToInt(param: String): Int = this.path(param)?.toIntOrNull() ?: throw AppException.InvalidData(param)

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())

    override fun serialize(
        encoder: Encoder,
        value: UUID,
    ) {
        encoder.encodeString(value.toString())
    }
}

inline fun <R> Connection.useWithRollback(block: (Connection) -> R): R {
    try {
        return block(this)
    } catch (e: Throwable) {
        rollback()
        throw e
    } finally {
        commit()
        close()
    }
}

fun kotlinx.datetime.LocalDate.toDate(): Date = Date.valueOf(this.toString())
