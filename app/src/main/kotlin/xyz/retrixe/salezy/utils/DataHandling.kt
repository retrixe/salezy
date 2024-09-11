package xyz.retrixe.salezy.utils

fun Long.asDecimal(scale: Int = 2): String {
    val padded = this.toString().padStart(scale + 1, '0')
    return padded.dropLast(scale) + "." + padded.takeLast(scale)
}

fun String.toDecimalLong(): Long = this.toBigDecimal().movePointRight(2).toLong()
