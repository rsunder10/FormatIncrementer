package com.rsunder10.formatincrementer

import com.intellij.ide.util.PropertiesComponent

/**
 * Persists the last-used [PatternConfig] across invocations using the application-level
 * [PropertiesComponent], so repeating the action reuses the previous pattern.
 */
object ConfigStore {
    private const val PREFIX = "com.rsunder10.formatincrementer."

    fun load(): PatternConfig {
        val p = PropertiesComponent.getInstance()
        val d = PatternConfig()
        return PatternConfig(
            type = runCatching { PatternType.valueOf(p.getValue(PREFIX + "type", d.type.name)) }
                .getOrDefault(d.type),
            start = p.getLong(PREFIX + "start", d.start),
            step = p.getLong(PREFIX + "step", d.step),
            width = p.getInt(PREFIX + "width", d.width),
            padChar = p.getValue(PREFIX + "padChar", d.padChar.toString()).firstOrNull() ?: d.padChar,
            radix = p.getInt(PREFIX + "radix", d.radix),
            uppercase = p.getBoolean(PREFIX + "uppercase", d.uppercase),
            startDate = p.getValue(PREFIX + "startDate", d.startDate),
            stepAmount = p.getLong(PREFIX + "stepAmount", d.stepAmount),
            stepUnit = runCatching { StepUnit.valueOf(p.getValue(PREFIX + "stepUnit", d.stepUnit.name)) }
                .getOrDefault(d.stepUnit),
            dateFormat = p.getValue(PREFIX + "dateFormat", d.dateFormat),
            template = p.getValue(PREFIX + "template", d.template),
        )
    }

    fun save(c: PatternConfig) {
        val p = PropertiesComponent.getInstance()
        p.setValue(PREFIX + "type", c.type.name)
        p.setValue(PREFIX + "start", c.start.toString())
        p.setValue(PREFIX + "step", c.step.toString())
        p.setValue(PREFIX + "width", c.width, PatternConfig().width)
        p.setValue(PREFIX + "padChar", c.padChar.toString())
        p.setValue(PREFIX + "radix", c.radix, PatternConfig().radix)
        p.setValue(PREFIX + "uppercase", c.uppercase)
        p.setValue(PREFIX + "startDate", c.startDate)
        p.setValue(PREFIX + "stepAmount", c.stepAmount.toString())
        p.setValue(PREFIX + "stepUnit", c.stepUnit.name)
        p.setValue(PREFIX + "dateFormat", c.dateFormat)
        p.setValue(PREFIX + "template", c.template)
    }
}
