package net.eltiburon.minimax.util

import java.util.Locale

/**
 * Formatea un monto entero como precio en pesos, con punto como separador de miles.
 * Ej.: 34000 -> "$34.000".
 *
 * Se centraliza acá (en vez de repetir la función en cada pantalla) para cumplir el
 * principio DRY: si mañana cambia el formato de precio, se modifica en un solo lugar.
 */
fun formatearPrecio(valor: Int): String =
    "$" + String.format(Locale.US, "%,d", valor).replace(",", ".")

/** Igual que [formatearPrecio] pero para montos con decimales (se redondea a entero). */
fun formatearPrecio(valor: Double): String = formatearPrecio(valor.toLong().toInt())
