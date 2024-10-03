package xyz.retrixe.salezy.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

fun Long.asDecimal(scale: Int = 2): String {
    val padded = this.toString().padStart(scale + 1, '0')
    return padded.dropLast(scale) + "." + padded.takeLast(scale)
}

fun String.toDecimalLong(): Long = this.toBigDecimal().movePointRight(2).toLong()

fun Long.toInstant(): Instant = Instant.ofEpochMilli(this)

fun Instant.formatted(): String = DateTimeFormatter
    .ofLocalizedDateTime(FormatStyle.SHORT)
    .withLocale(Locale.getDefault())
    .withZone(ZoneId.systemDefault()) // UTC -> system default
    .format(this)

fun ByteArray.toBase64String(): String = Base64.getEncoder().encodeToString(this)
