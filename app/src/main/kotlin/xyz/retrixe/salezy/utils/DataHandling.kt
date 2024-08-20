package xyz.retrixe.salezy.utils

fun Long.asDecimal(): String {
    return this.toString().padStart(3, '0').let {
        it.substring(0, it.length - 2) + "." + it.substring(it.length - 2)
    }
}
