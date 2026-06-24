package com.rsunder10.formatincrementer

import org.junit.Assert.assertEquals
import org.junit.Test

class SequenceTest {

    private fun render(config: PatternConfig, count: Int) =
        (0 until count).map { Sequence.render(config, it) }

    @Test
    fun numeric() {
        val c = PatternConfig(type = PatternType.NUMERIC, start = 1, step = 2)
        assertEquals(listOf("1", "3", "5"), render(c, 3))
    }

    @Test
    fun padding() {
        val c = PatternConfig(type = PatternType.PADDING, start = 1, step = 1, width = 3)
        assertEquals(listOf("001", "002", "003"), render(c, 3))
    }

    @Test
    fun radixHexUppercase() {
        val c = PatternConfig(type = PatternType.RADIX, start = 10, step = 5, radix = 16, uppercase = true)
        assertEquals(listOf("A", "F", "14"), render(c, 3))
    }

    @Test
    fun alpha() {
        val c = PatternConfig(type = PatternType.ALPHA, start = 1, step = 1)
        assertEquals(listOf("a", "b", "c"), render(c, 3))
        val c27 = PatternConfig(type = PatternType.ALPHA, start = 27, step = 1)
        assertEquals("aa", Sequence.render(c27, 0))
    }

    @Test
    fun roman() {
        val c = PatternConfig(type = PatternType.ROMAN, start = 1, step = 1, uppercase = true)
        assertEquals(listOf("I", "II", "III"), render(c, 3))
    }

    @Test
    fun dates() {
        val c = PatternConfig(
            type = PatternType.DATES,
            startDate = "2024-01-01T00:00:00",
            stepAmount = 1,
            stepUnit = StepUnit.DAYS,
            dateFormat = "yyyy-MM-dd",
        )
        assertEquals(listOf("2024-01-01", "2024-01-02", "2024-01-03"), render(c, 3))
    }

    @Test
    fun template() {
        val c = PatternConfig(type = PatternType.TEMPLATE, start = 1, step = 1, template = "row-{n:03}")
        assertEquals(listOf("row-001", "row-002"), render(c, 2))
        val hex = PatternConfig(type = PatternType.TEMPLATE, start = 10, step = 1, template = "0x{n:hex}")
        assertEquals("0xa", Sequence.render(hex, 0))
    }
}
