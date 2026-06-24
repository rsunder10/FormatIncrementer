# Format Incrementer — Help Guide

Fill every caret or selection in your editor with a generated **sequence** — not just plain `1, 2, 3`. Use numbers, zero-padded IDs, hex, letters, Roman numerals, dates, or a custom template.

---

## Quick start

| Step | What to do |
| --- | --- |
| **1** | **Place multiple carets** — hold `Alt` (`Option` on Mac) and click, or make multiple selections. |
| **2** | **Run the command** — use the keyboard shortcut, Tools menu, right-click menu, or Command Palette. |
| **3** | **Pick a pattern** — choose a type, set start/step (or other fields), check the live preview, then apply. |

---

## Keyboard shortcut

The **same chord** is used in IntelliJ and VS Code:

| Platform | Shortcut |
| --- | --- |
| **macOS** | `Shift + Cmd + 1`, then `Shift + Cmd + 1` again |
| **Windows / Linux** | `Shift + Ctrl + 1`, then `Shift + Ctrl + 1` again |

Press the combination **twice in a row** (a chord shortcut).

---

## Where to find it

### IntelliJ (IDEA, WebStorm, PyCharm, …)

| Location | Action |
| --- | --- |
| **Tools** menu | **Format Incrementer...** |
| **Editor** right-click | **Format Incrementer...** |
| **Tools** menu | **Format Incrementer Help** |
| **Help** menu | **Format Incrementer Help** |
| Pattern dialog | **Help** button (bottom-left) |

### VS Code

| Location | Action |
| --- | --- |
| **Command Palette** | `Format Incrementer: Fill Carets with Sequence` |
| **Editor** right-click | Same command |
| **Command Palette** | `Format Incrementer: Help` |

---

## How carets are filled

- Carets and selections are processed in **document order** (top → bottom, left → right).
- The first caret gets **index 0**, the second **index 1**, and so on.
- **Empty carets** → text is **inserted** at the caret.
- **Selections** → selected text is **replaced**.
- All changes happen in a **single undo step** — one Undo reverts everything.
- Your **last-used pattern is remembered** for the next run.

### Example

Place three carets and choose **Numeric** (start `1`, step `1`):

```
Before                          After
─────────────────────          ─────────────────────
line |one|                      line 1
line |two|                      line 2
line |three|                    line 3
```

---

## Pattern reference

| Pattern | Fields | Example output |
| --- | --- | --- |
| **Numeric** | start, step | `1, 3, 5` (start 1, step 2) |
| **Zero-padded** | start, step, width, pad char | `001, 002, 003` |
| **Radix** | start, step, base (2–36), uppercase | `a, f, 14` (hex from 10, step 5) |
| **Alphabetic** | start, step, uppercase | `a, b, c … z, aa, ab` |
| **Roman** | start, step, uppercase | `I, II, III, IV` |
| **Dates** | start date, step amount, unit, format | `2024-01-01, 2024-01-02, …` |
| **Template** | start, step, template string | `row-001, row-002, …` |

---

## Template tokens

Use `{n}` in a template as the counter placeholder:

| Token | Result (value 10) |
| --- | --- |
| `{n}` | `10` |
| `{n:03}` | `010` (zero-padded to width) |
| `{n:hex}` / `{n:HEX}` | `a` / `A` |
| `{n:oct}`, `{n:bin}` | `12`, `1010` |
| `{n:alpha}` / `{n:ALPHA}` | `j` / `J` |
| `{n:roman}` / `{n:ROMAN}` | `x` / `X` |

### Template examples

```
Template          Output
────────────────────────────────────
item_{n}          item_1, item_2, item_3
row-{n:03}        row-001, row-002, row-003
0x{n:hex}         0xa, 0xb, 0xc
page-{n:roman}    page-i, page-ii, page-iii
```

---

## Tips

> **Quick repeat** — Run the command again to reuse your last pattern. In IntelliJ, just hit **Apply**; in VS Code, confirm each prompt with the remembered defaults.

> **Descending sequences** — Use a negative step (e.g. start `10`, step `-1` → `10, 9, 8, …`).

> **Original auto-increment** — Choose **Numeric**, start `1`, step `1` for the same behavior as the legacy plugin.

> **Date formats** — Use `yyyy`, `MM`, `dd`, `HH`, `mm`, `ss` in the format field (e.g. `yyyy-MM-dd`).

---

## Troubleshooting

| Issue | Solution |
| --- | --- |
| Shortcut does nothing | Press the chord **twice** — it is a two-step shortcut, not a single keypress. |
| Same value at every caret | Check that carets are in different positions; only one caret means only one value. |
| Roman shows a number | Roman numerals only work for values 1–3999; outside that range the plain number is shown. |
| Date looks wrong | Use ISO format for start date (`2024-01-01` or `2024-01-01T09:00:00`). |

---

*Format Incrementer 2.0 — by rsunder10 (RedEyeGuy)*
