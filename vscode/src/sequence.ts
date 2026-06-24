/**
 * Pure sequence engine for the VSCode extension. Mirrors the Kotlin engine in
 * `intellij/src/main/kotlin/com/rsunder10/formatincrementer/Sequence.kt` - keep both
 * in sync when changing behavior.
 */

export type PatternType =
  | "numeric"
  | "padding"
  | "radix"
  | "alpha"
  | "roman"
  | "dates"
  | "template";

export type StepUnit =
  | "seconds"
  | "minutes"
  | "hours"
  | "days"
  | "weeks"
  | "months"
  | "years";

export interface PatternConfig {
  type: PatternType;
  start: number;
  step: number;
  width: number;
  padChar: string;
  radix: number;
  uppercase: boolean;
  startDate: string;
  stepAmount: number;
  stepUnit: StepUnit;
  dateFormat: string;
  template: string;
}

export const DEFAULT_CONFIG: PatternConfig = {
  type: "numeric",
  start: 1,
  step: 1,
  width: 3,
  padChar: "0",
  radix: 16,
  uppercase: false,
  startDate: "2024-01-01T00:00:00",
  stepAmount: 1,
  stepUnit: "days",
  dateFormat: "yyyy-MM-dd",
  template: "item_{n}",
};

export function render(config: PatternConfig, index: number): string {
  switch (config.type) {
    case "numeric":
      return String(numericValue(config, index));
    case "padding":
      return pad(numericValue(config, index), config.width, config.padChar);
    case "radix":
      return radix(numericValue(config, index), config.radix, config.uppercase);
    case "alpha":
      return alpha(numericValue(config, index), config.uppercase);
    case "roman":
      return roman(numericValue(config, index), config.uppercase);
    case "dates":
      return dateValue(config, index);
    case "template":
      return template(config, index);
  }
}

export function preview(config: PatternConfig, count = 5): string[] {
  const out: string[] = [];
  for (let i = 0; i < count; i++) {
    out.push(render(config, i));
  }
  return out;
}

function numericValue(config: PatternConfig, index: number): number {
  return config.start + index * config.step;
}

function pad(value: number, width: number, padChar: string): string {
  const negative = value < 0;
  const digits = Math.abs(value).toString();
  const target = negative ? width - 1 : width;
  const ch = padChar.length > 0 ? padChar[0] : "0";
  const padded =
    digits.length >= target ? digits : ch.repeat(target - digits.length) + digits;
  return negative ? `-${padded}` : padded;
}

function radix(value: number, base: number, uppercase: boolean): string {
  const safeBase = Math.min(36, Math.max(2, Math.trunc(base)));
  const s = value.toString(safeBase);
  return uppercase ? s.toUpperCase() : s;
}

/** Excel-style: 1 -> a, 26 -> z, 27 -> aa. Non-positive values fall back to the number. */
function alpha(value: number, uppercase: boolean): string {
  if (value <= 0) {
    return String(value);
  }
  let n = Math.trunc(value);
  let result = "";
  while (n > 0) {
    const rem = (n - 1) % 26;
    result = String.fromCharCode(97 + rem) + result;
    n = Math.floor((n - 1) / 26);
  }
  return uppercase ? result.toUpperCase() : result;
}

const ROMAN_TABLE: Array<[number, string]> = [
  [1000, "M"],
  [900, "CM"],
  [500, "D"],
  [400, "CD"],
  [100, "C"],
  [90, "XC"],
  [50, "L"],
  [40, "XL"],
  [10, "X"],
  [9, "IX"],
  [5, "V"],
  [4, "IV"],
  [1, "I"],
];

function roman(value: number, uppercase: boolean): string {
  if (value <= 0 || value >= 4000) {
    return String(value);
  }
  let n = Math.trunc(value);
  let result = "";
  for (const [num, sym] of ROMAN_TABLE) {
    while (n >= num) {
      result += sym;
      n -= num;
    }
  }
  return uppercase ? result : result.toLowerCase();
}

const UNIT_MS: Record<Exclude<StepUnit, "months" | "years">, number> = {
  seconds: 1000,
  minutes: 60 * 1000,
  hours: 60 * 60 * 1000,
  days: 24 * 60 * 60 * 1000,
  weeks: 7 * 24 * 60 * 60 * 1000,
};

function dateValue(config: PatternConfig, index: number): string {
  const base = parseDate(config.startDate);
  if (!base) {
    return config.startDate;
  }
  const moved = new Date(base.getTime());
  const amount = config.stepAmount * index;
  if (config.stepUnit === "years") {
    moved.setFullYear(moved.getFullYear() + amount);
  } else if (config.stepUnit === "months") {
    moved.setMonth(moved.getMonth() + amount);
  } else {
    moved.setTime(moved.getTime() + amount * UNIT_MS[config.stepUnit]);
  }
  return formatDate(moved, config.dateFormat);
}

function parseDate(text: string): Date | null {
  const candidates = [text, `${text}T00:00:00`];
  for (const c of candidates) {
    const d = new Date(c);
    if (!isNaN(d.getTime())) {
      return d;
    }
  }
  return null;
}

/** Minimal formatter supporting the common pattern tokens used by the defaults. */
function formatDate(date: Date, pattern: string): string {
  const pad2 = (n: number) => String(n).padStart(2, "0");
  const tokens: Record<string, string> = {
    yyyy: String(date.getFullYear()),
    yy: String(date.getFullYear()).slice(-2),
    MM: pad2(date.getMonth() + 1),
    dd: pad2(date.getDate()),
    HH: pad2(date.getHours()),
    mm: pad2(date.getMinutes()),
    ss: pad2(date.getSeconds()),
  };
  return pattern.replace(/yyyy|yy|MM|dd|HH|mm|ss/g, (m) => tokens[m] ?? m);
}

const TOKEN_REGEX = /\{n(?::([^}]*))?\}/g;

/**
 * Replaces every `{n}` token in the template with the formatted counter.
 * Supported specs: `{n}`, `{n:03}`, `{n:hex}`/`{n:HEX}`, `{n:oct}`, `{n:bin}`,
 * `{n:alpha}`/`{n:ALPHA}`, `{n:roman}`/`{n:ROMAN}`.
 */
function template(config: PatternConfig, index: number): string {
  const value = numericValue(config, index);
  return config.template.replace(TOKEN_REGEX, (_m, spec: string | undefined) =>
    formatToken(value, spec ?? "")
  );
}

function formatToken(value: number, spec: string): string {
  if (spec === "") {
    return String(value);
  }
  const lower = spec.toLowerCase();
  if (lower === "hex") {
    return radix(value, 16, spec === "HEX");
  }
  if (lower === "oct") {
    return radix(value, 8, false);
  }
  if (lower === "bin") {
    return radix(value, 2, false);
  }
  if (lower === "alpha") {
    return alpha(value, spec === "ALPHA");
  }
  if (lower === "roman") {
    return roman(value, spec === "ROMAN");
  }
  if (/^\d+$/.test(spec)) {
    return pad(value, parseInt(spec, 10), "0");
  }
  return String(value);
}
