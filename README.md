# Format Incrementer

Fill every caret / selection in your editor with a generated **sequence** — not just
plain `1, 2, 3` auto-increment. Drop multiple carets (or make multiple selections),
invoke the command, pick a pattern, preview it live, and apply.

This repo is a monorepo with two feature-equal plugins that share an identical
sequence engine:

| Plugin | Folder | Stack |
| --- | --- | --- |
| IntelliJ Platform (IDEA, WebStorm, PyCharm, ...) | [`intellij/`](intellij/) | Kotlin + Gradle (IntelliJ Platform Gradle Plugin 2.x) |
| Visual Studio Code | [`vscode/`](vscode/) | TypeScript |

> Originally a 2016 IntelliJ-only plugin that only did sequential numbering. Version
> 2.0 is a full rewrite onto modern tooling, adds a VSCode extension, and replaces the
> hard-coded counter with a configurable pattern picker.

---

## Help & usage guide

> Full guide: **[docs/HELP.md](docs/HELP.md)** · In-app: **Format Incrementer Help** (Tools menu / Command Palette)

### Quick start

```
  1. Place carets     Alt/Option-click, or multiple selections
  2. Run command      Shortcut, Tools menu, or right-click
  3. Pick pattern     Choose type → set fields → check preview → Apply
```

### Keyboard shortcut (same in both plugins)

| Platform | Shortcut |
| --- | --- |
| **macOS** | `Shift + Cmd + 1` |
| **Windows / Linux** | `Shift + Ctrl + 1` |

### Where to find it

| | IntelliJ | VS Code |
| --- | --- | --- |
| **Run** | Tools → Format Incrementer… · Editor right-click | Command Palette · Editor right-click |
| **Help** | Tools → Format Incrementer Help · Help menu · **Help** button in dialog | Command Palette → Format Incrementer: Help |

### Example workflow

```
  Before (3 carets, numeric start=1 step=1)     After
  ────────────────────────────────────────      ─────────
  id: |___|                                     id: 1
  id: |___|                                     id: 2
  id: |___|                                     id: 3
```

All carets update in **document order** in a **single undo step**. Your last pattern is remembered for the next run.

---

## Patterns

All pattern types are available in both plugins:

| Pattern | Parameters | Example output |
| --- | --- | --- |
| Numeric | start, step | `1, 3, 5` (start 1, step 2) |
| Zero-padded | start, step, width, pad char | `001, 002, 003` |
| Radix | start, step, base (2-36), uppercase | `a, f, 14` (hex from 10, step 5) |
| Alphabetic | start, step, uppercase | `a, b, c ... aa, ab` |
| Roman | start, step, uppercase | `I, II, III` |
| Dates | start date, step amount, step unit, format | `2024-01-01, 2024-01-02, ...` |
| Template | start, step, template string | `item_1`, `row-001`, `0xa` |

### Template tokens

The template pattern replaces a `{n}` token with the formatted counter:

| Token | Result (value 10) |
| --- | --- |
| `{n}` | `10` |
| `{n:03}` | `010` (zero-padded width) |
| `{n:hex}` / `{n:HEX}` | `a` / `A` |
| `{n:oct}`, `{n:bin}` | `12`, `1010` |
| `{n:alpha}` / `{n:ALPHA}` | `j` / `J` |
| `{n:roman}` / `{n:ROMAN}` | `x` / `X` |

**Examples:** `item_{n}` → `item_1, item_2` · `row-{n:03}` → `row-001, row-002` · `0x{n:hex}` → `0xa, 0xb`

Descending sequences are supported via a negative step.

---

## Building

### IntelliJ plugin

Requires JDK 21 for **compilation** (auto-provisioned via the Gradle toolchain resolver).
Gradle itself **cannot run on JDK 25** — if `./gradlew` fails with `25.0.1`, the wrapper
auto-selects a compatible JDK from common install locations. If none is found, run the
one-time setup script:

```bash
cd intellij
./scripts/ensure-jdk.sh   # downloads JDK 21 into .jdk/ (gitignored)
./gradlew runIde
```

Or use the convenience launcher:

```bash
cd intellij
./run-ide.sh
```

```bash
cd intellij
./gradlew test          # run the engine unit tests
./gradlew buildPlugin    # produces build/distributions/*.zip
./gradlew runIde         # launch a sandbox IDE with the plugin loaded
```

Install the built ZIP via *Settings > Plugins > Install Plugin from Disk*.

### VSCode extension

Requires Node.js 18+.

```bash
cd vscode
npm install
npm test                 # run the engine unit tests
npm run build            # bundle dist/extension.js
npx vsce package         # produces a .vsix (requires @vscode/vsce)
```

Install the `.vsix` via *Extensions view > ... > Install from VSIX*, or press `F5` in
VSCode to launch an Extension Development Host.

---

## Architecture

The two plugins intentionally **duplicate** a small, pure sequence engine rather than
sharing a package across language ecosystems:

- IntelliJ: [`intellij/src/main/kotlin/com/rsunder10/formatincrementer/Sequence.kt`](intellij/src/main/kotlin/com/rsunder10/formatincrementer/Sequence.kt)
- VSCode: [`vscode/src/sequence.ts`](vscode/src/sequence.ts)

Both expose a pure `render(config, index)` that maps a 0-based caret order to a string,
plus a `preview(...)` helper used by the UIs. Each side has matching unit tests
([Kotlin](intellij/src/test/kotlin/com/rsunder10/formatincrementer/SequenceTest.kt),
[TypeScript](vscode/src/test/sequence.test.ts)) covering every pattern; keep them in
sync when changing behavior.

---

## License

MIT - see [LICENSE](LICENSE).
