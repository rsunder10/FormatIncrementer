package com.rsunder10.formatincrementer

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

/**
 * The kind of sequence to generate. Mirrors `PatternType` in the VSCode extension
 * (`vscode/src/sequence.ts`) - keep both in sync.
 */
enum class PatternType {
    NUMERIC,
    PADDING,
    RADIX,
    ALPHA,
    ROMAN,
    DATES,
    TEMPLATE,
}

enum class StepUnit {
    SECONDS,
    MINUTES,
    HOURS,
    DAYS,
    WEEKS,
    MONTHS,
    YEARS;

    fun toChrono(): ChronoUnit = when (this) {
        SECONDS -> ChronoUnit.SECONDS
        MINUTES -> ChronoUnit.MINUTES
        HOURS -> ChronoUnit.HOURS
        DAYS -> ChronoUnit.DAYS
        WEEKS -> ChronoUnit.WEEKS
        MONTHS -> ChronoUnit.MONTHS
        YEARS -> ChronoUnit.YEARS
    }
}

/**
 * A complete description of a sequence. Only the fields relevant to [type] are used;
 * the rest fall back to defaults so the config is cheap to persist as a flat record.
 */
data class PatternConfig(
    val type: PatternType = PatternType.NUMERIC,
    val start: Long = 1,
    val step: Long = 1,
    val width: Int = 3,
    val padChar: Char = '0',
    val radix: Int = 16,
    val uppercase: Boolean = false,
    val startDate: String = "2024-01-01T00:00:00",
    val stepAmount: Long = 1,
    val stepUnit: StepUnit = StepUnit.DAYS,
    val dateFormat: String = "yyyy-MM-dd",
    val template: String = "item_{n}",
)

/**
 * Pure sequence engine. [index] is the 0-based order of the caret/selection.
 */
object Sequence {

    fun render(config: PatternConfig, index: Int): String = when (config.type) {
        PatternType.NUMERIC -> numericValue(config, index).toString()
        PatternType.PADDING -> pad(numericValue(config, index), config.width, config.padChar)
        PatternType.RADIX -> radix(numericValue(config, index), config.radix, config.uppercase)
        PatternType.ALPHA -> alpha(numericValue(config, index), config.uppercase)
        PatternType.ROMAN -> roman(numericValue(config, index), config.uppercase)
        PatternType.DATES -> dateValue(config, index)
        PatternType.TEMPLATE -> template(config, index)
    }

    /** Renders the first [count] values, for live previews. */
    fun preview(config: PatternConfig, count: Int = 5): List<String> =
        (0 until count).map { render(config, it) }

    private fun numericValue(config: PatternConfig, index: Int): Long =
        config.start + index.toLong() * config.step

    private fun pad(value: Long, width: Int, padChar: Char): String {
        val negative = value < 0
        val digits = kotlin.math.abs(value).toString()
        val targetWidth = if (negative) width - 1 else width
        val padded = if (digits.length >= targetWidth) digits
        else padChar.toString().repeat(targetWidth - digits.length) + digits
        return if (negative) "-$padded" else padded
    }

    private fun radix(value: Long, radix: Int, uppercase: Boolean): String {
        val safeRadix = radix.coerceIn(2, 36)
        val s = value.toString(safeRadix)
        return if (uppercase) s.uppercase() else s
    }

    /** Excel-style: 1 -> a, 26 -> z, 27 -> aa. Non-positive values fall back to the number. */
    private fun alpha(value: Long, uppercase: Boolean): String {
        if (value <= 0) return value.toString()
        val sb = StringBuilder()
        var n = value
        while (n > 0) {
            val rem = ((n - 1) % 26).toInt()
            sb.append(('a' + rem))
            n = (n - 1) / 26
        }
        val result = sb.reverse().toString()
        return if (uppercase) result.uppercase() else result
    }

    private val ROMAN_TABLE = listOf(
        1000 to "M", 900 to "CM", 500 to "D", 400 to "CD",
        100 to "C", 90 to "XC", 50 to "L", 40 to "XL",
        10 to "X", 9 to "IX", 5 to "V", 4 to "IV", 1 to "I",
    )

    private fun roman(value: Long, uppercase: Boolean): String {
        if (value <= 0 || value >= 4000) return value.toString()
        var n = value.toInt()
        val sb = StringBuilder()
        for ((num, sym) in ROMAN_TABLE) {
            while (n >= num) {
                sb.append(sym)
                n -= num
            }
        }
        val result = sb.toString()
        return if (uppercase) result else result.lowercase()
    }

    private fun dateValue(config: PatternConfig, index: Int): String {
        val base = parseDate(config.startDate) ?: return config.startDate
        val moved = base.plus(index.toLong() * config.stepAmount, config.stepUnit.toChrono())
        return try {
            moved.format(DateTimeFormatter.ofPattern(config.dateFormat))
        } catch (_: IllegalArgumentException) {
            moved.toString()
        }
    }

    private fun parseDate(text: String): LocalDateTime? = try {
        LocalDateTime.parse(text)
    } catch (_: DateTimeParseException) {
        try {
            LocalDateTime.parse(text + "T00:00:00")
        } catch (_: DateTimeParseException) {
            null
        }
    }

    private val TOKEN_REGEX = Regex("""\{n(?::([^}]*))?}""")

    /**
     * Replaces every `{n}` token in [PatternConfig.template] with the formatted counter.
     * Supported specs: `{n}`, `{n:03}` (zero-pad width), `{n:hex}`/`{n:HEX}`, `{n:oct}`,
     * `{n:bin}`, `{n:alpha}`/`{n:ALPHA}`, `{n:roman}`/`{n:ROMAN}`.
     */
    private fun template(config: PatternConfig, index: Int): String {
        val value = numericValue(config, index)
        return TOKEN_REGEX.replace(config.template) { match ->
            val spec = match.groupValues[1]
            formatToken(value, spec)
        }
    }

    private fun formatToken(value: Long, spec: String): String = when {
        spec.isEmpty() -> value.toString()
        spec.equals("hex", true) -> radix(value, 16, spec == "HEX")
        spec.equals("oct", true) -> radix(value, 8, false)
        spec.equals("bin", true) -> radix(value, 2, false)
        spec.equals("alpha", true) -> alpha(value, spec == "ALPHA")
        spec.equals("roman", true) -> roman(value, spec == "ROMAN")
        spec.all { it.isDigit() } -> pad(value, spec.toInt(), '0')
        spec.startsWith("0") && spec.drop(1).all { it.isDigit() } -> pad(value, spec.length, '0')
        else -> value.toString()
    }
}
