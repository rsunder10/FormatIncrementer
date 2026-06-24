import * as assert from "assert";
import { PatternConfig, DEFAULT_CONFIG, render } from "../sequence";

function make(overrides: Partial<PatternConfig>): PatternConfig {
  return { ...DEFAULT_CONFIG, ...overrides };
}

function seq(config: PatternConfig, count: number): string[] {
  const out: string[] = [];
  for (let i = 0; i < count; i++) {
    out.push(render(config, i));
  }
  return out;
}

const tests: Array<[string, () => void]> = [
  [
    "numeric",
    () =>
      assert.deepStrictEqual(
        seq(make({ type: "numeric", start: 1, step: 2 }), 3),
        ["1", "3", "5"]
      ),
  ],
  [
    "padding",
    () =>
      assert.deepStrictEqual(
        seq(make({ type: "padding", start: 1, step: 1, width: 3 }), 3),
        ["001", "002", "003"]
      ),
  ],
  [
    "radix hex uppercase",
    () =>
      assert.deepStrictEqual(
        seq(make({ type: "radix", start: 10, step: 5, radix: 16, uppercase: true }), 3),
        ["A", "F", "14"]
      ),
  ],
  [
    "alpha",
    () => {
      assert.deepStrictEqual(seq(make({ type: "alpha", start: 1, step: 1 }), 3), [
        "a",
        "b",
        "c",
      ]);
      assert.strictEqual(render(make({ type: "alpha", start: 27, step: 1 }), 0), "aa");
    },
  ],
  [
    "roman",
    () =>
      assert.deepStrictEqual(
        seq(make({ type: "roman", start: 1, step: 1, uppercase: true }), 3),
        ["I", "II", "III"]
      ),
  ],
  [
    "dates",
    () =>
      assert.deepStrictEqual(
        seq(
          make({
            type: "dates",
            startDate: "2024-01-01T00:00:00",
            stepAmount: 1,
            stepUnit: "days",
            dateFormat: "yyyy-MM-dd",
          }),
          3
        ),
        ["2024-01-01", "2024-01-02", "2024-01-03"]
      ),
  ],
  [
    "template",
    () => {
      assert.deepStrictEqual(
        seq(make({ type: "template", start: 1, step: 1, template: "row-{n:03}" }), 2),
        ["row-001", "row-002"]
      );
      assert.strictEqual(
        render(make({ type: "template", start: 10, step: 1, template: "0x{n:hex}" }), 0),
        "0xa"
      );
    },
  ],
];

let failed = 0;
for (const [name, fn] of tests) {
  try {
    fn();
    console.log(`ok - ${name}`);
  } catch (e) {
    failed++;
    console.error(`FAIL - ${name}`);
    console.error(e);
  }
}

if (failed > 0) {
  console.error(`${failed} test(s) failed`);
  process.exit(1);
} else {
  console.log(`All ${tests.length} tests passed`);
}
