import * as vscode from "vscode";
import {
  DEFAULT_CONFIG,
  PatternConfig,
  PatternType,
  StepUnit,
  preview,
} from "./sequence";

interface TypeItem extends vscode.QuickPickItem {
  type: PatternType;
}

const TYPE_ITEMS: TypeItem[] = [
  { type: "numeric", label: "$(symbol-number) Numeric", description: "start + step (1, 2, 3 ...)" },
  { type: "padding", label: "$(symbol-number) Zero-padded", description: "fixed width (001, 002 ...)" },
  { type: "radix", label: "$(symbol-numeric) Radix", description: "hex / oct / bin / custom base" },
  { type: "alpha", label: "$(symbol-key) Alphabetic", description: "a, b, c ... aa, ab" },
  { type: "roman", label: "$(symbol-constant) Roman", description: "I, II, III ..." },
  { type: "dates", label: "$(calendar) Dates", description: "step by day / month / etc." },
  { type: "template", label: "$(symbol-string) Template", description: "custom text with {n} token" },
];

const STEP_UNITS: StepUnit[] = [
  "seconds",
  "minutes",
  "hours",
  "days",
  "weeks",
  "months",
  "years",
];

/**
 * Drives the inline pattern picker: choose a type, fill in the relevant fields, and
 * confirm. Returns the chosen config, or undefined if the user cancelled.
 */
export async function pickPattern(
  initial: PatternConfig
): Promise<PatternConfig | undefined> {
  const type = await pickType(initial.type);
  if (!type) {
    return undefined;
  }

  const config: PatternConfig = { ...initial, type };

  switch (type) {
    case "numeric":
      return collect(config, ["start", "step"]);
    case "padding":
      return collect(config, ["start", "step", "width", "padChar"]);
    case "radix":
      return collect(config, ["start", "step", "radix", "uppercase"]);
    case "alpha":
    case "roman":
      return collect(config, ["start", "step", "uppercase"]);
    case "dates":
      return collect(config, ["startDate", "stepAmount", "stepUnit", "dateFormat"]);
    case "template":
      return collect(config, ["start", "step", "template"]);
  }
}

function pickType(current: PatternType): Promise<PatternType | undefined> {
  return new Promise((resolve) => {
    const qp = vscode.window.createQuickPick<TypeItem>();
    qp.title = "Format Incrementer - choose a pattern";
    qp.placeholder = "Select the sequence pattern to fill carets with";
    qp.items = TYPE_ITEMS;
    qp.activeItems = TYPE_ITEMS.filter((i) => i.type === current);
    let accepted = false;
    qp.onDidAccept(() => {
      accepted = true;
      const sel = qp.selectedItems[0];
      qp.hide();
      resolve(sel?.type);
    });
    qp.onDidHide(() => {
      qp.dispose();
      if (!accepted) {
        resolve(undefined);
      }
    });
    qp.show();
  });
}

type Field =
  | "start"
  | "step"
  | "width"
  | "padChar"
  | "radix"
  | "uppercase"
  | "startDate"
  | "stepAmount"
  | "stepUnit"
  | "dateFormat"
  | "template";

/** Sequentially prompts for each field, showing a live preview in the prompt text. */
async function collect(
  config: PatternConfig,
  fields: Field[]
): Promise<PatternConfig | undefined> {
  const result = { ...config };
  for (const field of fields) {
    const next = await promptField(result, field);
    if (next === undefined) {
      return undefined;
    }
    Object.assign(result, next);
  }
  return result;
}

function previewText(config: PatternConfig): string {
  return `Preview: ${preview(config, 5).join(", ")} ...`;
}

async function promptField(
  config: PatternConfig,
  field: Field
): Promise<Partial<PatternConfig> | undefined> {
  switch (field) {
    case "uppercase": {
      const choice = await vscode.window.showQuickPick(["No", "Yes"], {
        title: "Format Incrementer",
        placeHolder: `Uppercase? (${previewText({ ...config, uppercase: true })})`,
      });
      if (choice === undefined) {
        return undefined;
      }
      return { uppercase: choice === "Yes" };
    }
    case "stepUnit": {
      const choice = await vscode.window.showQuickPick(STEP_UNITS, {
        title: "Format Incrementer",
        placeHolder: "Step unit",
      });
      if (choice === undefined) {
        return undefined;
      }
      return { stepUnit: choice as StepUnit };
    }
    default:
      return promptTextField(config, field);
  }
}

interface TextFieldSpec {
  prompt: string;
  value: (c: PatternConfig) => string;
  parse: (raw: string) => Partial<PatternConfig> | undefined;
  validate?: (raw: string) => string | undefined;
}

const TEXT_FIELDS: Record<
  Exclude<Field, "uppercase" | "stepUnit">,
  TextFieldSpec
> = {
  start: {
    prompt: "Start value (integer)",
    value: (c) => String(c.start),
    parse: (raw) => ({ start: parseInt(raw, 10) }),
    validate: intValidator,
  },
  step: {
    prompt: "Step (integer, negative allowed)",
    value: (c) => String(c.step),
    parse: (raw) => ({ step: parseInt(raw, 10) }),
    validate: intValidator,
  },
  width: {
    prompt: "Width (total digits)",
    value: (c) => String(c.width),
    parse: (raw) => ({ width: parseInt(raw, 10) }),
    validate: intValidator,
  },
  padChar: {
    prompt: "Pad character",
    value: (c) => c.padChar,
    parse: (raw) => ({ padChar: raw.length > 0 ? raw[0] : "0" }),
  },
  radix: {
    prompt: "Radix / base (2-36)",
    value: (c) => String(c.radix),
    parse: (raw) => ({ radix: parseInt(raw, 10) }),
    validate: (raw) => {
      const n = parseInt(raw, 10);
      return Number.isInteger(n) && n >= 2 && n <= 36
        ? undefined
        : "Radix must be an integer 2-36";
    },
  },
  startDate: {
    prompt: "Start date (e.g. 2024-01-01 or 2024-01-01T09:00:00)",
    value: (c) => c.startDate,
    parse: (raw) => ({ startDate: raw.trim() }),
    validate: (raw) =>
      isNaN(new Date(raw.trim()).getTime()) &&
      isNaN(new Date(`${raw.trim()}T00:00:00`).getTime())
        ? "Not a valid date"
        : undefined,
  },
  stepAmount: {
    prompt: "Step amount (integer)",
    value: (c) => String(c.stepAmount),
    parse: (raw) => ({ stepAmount: parseInt(raw, 10) }),
    validate: intValidator,
  },
  dateFormat: {
    prompt: "Date format (yyyy, MM, dd, HH, mm, ss)",
    value: (c) => c.dateFormat,
    parse: (raw) => ({ dateFormat: raw }),
  },
  template: {
    prompt: "Template - tokens: {n}, {n:03}, {n:hex}, {n:alpha}, {n:roman}",
    value: (c) => c.template,
    parse: (raw) => ({ template: raw }),
  },
};

function intValidator(raw: string): string | undefined {
  return /^-?\d+$/.test(raw.trim()) ? undefined : "Must be an integer";
}

async function promptTextField(
  config: PatternConfig,
  field: Exclude<Field, "uppercase" | "stepUnit">
): Promise<Partial<PatternConfig> | undefined> {
  const spec = TEXT_FIELDS[field];
  const raw = await vscode.window.showInputBox({
    title: "Format Incrementer",
    prompt: `${spec.prompt}    |    ${previewText(config)}`,
    value: spec.value(config),
    validateInput: spec.validate,
  });
  if (raw === undefined) {
    return undefined;
  }
  return spec.parse(raw);
}

export { DEFAULT_CONFIG };
