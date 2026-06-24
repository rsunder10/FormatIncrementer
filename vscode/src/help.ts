import * as vscode from "vscode";

/** Keep in sync with intellij/.../HelpContent.kt and docs/HELP.md */
export const SHORTCUT_MAC = "Shift + Cmd + 1, then Shift + Cmd + 1";
export const SHORTCUT_WIN = "Shift + Ctrl + 1, then Shift + Ctrl + 1";

export function getHelpHtml(): string {
  return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <style>
    :root {
      --bg: var(--vscode-editor-background, #1e1e1e);
      --fg: var(--vscode-editor-foreground, #d4d4d4);
      --muted: var(--vscode-descriptionForeground, #9d9d9d);
      --border: var(--vscode-panel-border, #3c3c3c);
      --accent: var(--vscode-textLink-foreground, #3794ff);
      --tip-bg: var(--vscode-textBlockQuote-background, #2a2a2a);
      --code-bg: var(--vscode-textCodeBlock-background, #2d2d2d);
      --table-head: var(--vscode-list-hoverBackground, #2a2d2e);
    }
    * { box-sizing: border-box; }
    body {
      font-family: var(--vscode-font-family, -apple-system, BlinkMacSystemFont, sans-serif);
      font-size: var(--vscode-font-size, 13px);
      line-height: 1.55;
      color: var(--fg);
      background: var(--bg);
      margin: 0;
      padding: 20px 24px 32px;
      max-width: 720px;
    }
    h1 { font-size: 1.5rem; font-weight: 600; margin: 0 0 6px; letter-spacing: -0.02em; }
    h2 {
      font-size: 1rem; font-weight: 600; margin: 28px 0 10px;
      padding-bottom: 6px; border-bottom: 1px solid var(--border);
    }
    h3 { font-size: 0.92rem; font-weight: 600; margin: 16px 0 8px; }
    p { margin: 8px 0; }
    ul, ol { margin: 8px 0; padding-left: 1.4rem; }
    li { margin: 5px 0; }
    code, kbd {
      font-family: var(--vscode-editor-font-family, Consolas, monospace);
      font-size: 0.92em;
    }
    code {
      background: var(--code-bg);
      padding: 2px 6px;
      border-radius: 4px;
    }
    kbd {
      background: var(--table-head);
      border: 1px solid var(--border);
      border-bottom-width: 2px;
      padding: 2px 7px;
      border-radius: 4px;
    }
    table { width: 100%; border-collapse: collapse; margin: 10px 0; font-size: 0.92em; }
    th, td { text-align: left; padding: 8px 10px; border: 1px solid var(--border); vertical-align: top; }
    th { background: var(--table-head); font-weight: 600; }
    .lead { color: var(--muted); font-size: 1.02em; margin-bottom: 16px; }
    .shortcut {
      display: inline-block;
      background: var(--tip-bg);
      border: 1px solid var(--border);
      padding: 6px 12px;
      border-radius: 6px;
      margin: 4px 8px 4px 0;
      font-size: 0.92em;
    }
    .tip {
      background: var(--tip-bg);
      border-left: 3px solid var(--accent);
      padding: 10px 14px;
      margin: 12px 0;
      border-radius: 0 6px 6px 0;
    }
    .example {
      background: var(--code-bg);
      border: 1px solid var(--border);
      padding: 12px 14px;
      border-radius: 8px;
      font-family: var(--vscode-editor-font-family, Consolas, monospace);
      font-size: 0.88em;
      white-space: pre;
      overflow-x: auto;
      margin: 10px 0;
      line-height: 1.45;
    }
    .steps { counter-reset: step; list-style: none; padding: 0; }
    .steps li {
      counter-increment: step;
      position: relative;
      padding: 10px 12px 10px 44px;
      margin: 8px 0;
      background: var(--tip-bg);
      border-radius: 8px;
      border: 1px solid var(--border);
    }
    .steps li::before {
      content: counter(step);
      position: absolute;
      left: 12px;
      top: 10px;
      width: 22px; height: 22px;
      background: var(--accent);
      color: #fff;
      border-radius: 50%;
      font-size: 0.78em;
      font-weight: 700;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    footer { margin-top: 28px; color: var(--muted); font-size: 0.85em; }
  </style>
</head>
<body>
  <h1>Format Incrementer</h1>
  <p class="lead">Fill every caret or selection with a generated sequence — numbers, letters, dates, Roman numerals, hex, or your own template.</p>

  <h2>Quick start</h2>
  <ol class="steps">
    <li><strong>Place multiple carets</strong> — hold <kbd>Alt</kbd> (<kbd>Option</kbd> on Mac) and click, or make multiple selections.</li>
    <li><strong>Run the command</strong> — Command Palette → <em>Format Incrementer: Fill Carets with Sequence</em>, right-click in the editor, or use the shortcut below.</li>
    <li><strong>Pick a pattern</strong> — choose a type, set start/step (or other fields), check the live preview, then confirm.</li>
  </ol>

  <h2>Keyboard shortcut</h2>
  <p>The same chord is used in VS Code and IntelliJ:</p>
  <p><span class="shortcut"><strong>macOS:</strong> ${SHORTCUT_MAC}</span></p>
  <p><span class="shortcut"><strong>Windows / Linux:</strong> ${SHORTCUT_WIN}</span></p>
  <p>Press the combination <strong>twice in a row</strong> (a chord shortcut).</p>

  <h2>Where to find it</h2>
  <table>
    <tr><th>Location</th><th>Action</th></tr>
    <tr><td>Command Palette</td><td><strong>Format Incrementer: Fill Carets with Sequence</strong></td></tr>
    <tr><td>Editor right-click</td><td><strong>Format Incrementer: Fill Carets with Sequence</strong></td></tr>
    <tr><td>Command Palette</td><td><strong>Format Incrementer: Help</strong> (this guide)</td></tr>
  </table>

  <h2>How carets are filled</h2>
  <ul>
    <li>Carets and selections are processed in <strong>document order</strong> (top to bottom, left to right).</li>
    <li>The first caret gets index 0, the second index 1, and so on.</li>
    <li>Empty carets <strong>insert</strong> text; selections are <strong>replaced</strong>.</li>
    <li>All changes happen in a <strong>single undo step</strong> — press Undo once to revert everything.</li>
    <li>Your last-used pattern is remembered for the next run.</li>
  </ul>

  <div class="example">Example — numeric, start 1, step 1:

  line |one|
  line |two|
  line |three|

  becomes:

  line 1
  line 2
  line 3</div>

  <h2>Pattern reference</h2>
  <table>
    <tr><th>Pattern</th><th>Fields</th><th>Example output</th></tr>
    <tr><td><strong>Numeric</strong></td><td>start, step</td><td><code>1, 3, 5</code> (start 1, step 2)</td></tr>
    <tr><td><strong>Zero-padded</strong></td><td>start, step, width, pad char</td><td><code>001, 002, 003</code></td></tr>
    <tr><td><strong>Radix</strong></td><td>start, step, base (2–36), uppercase</td><td><code>a, f, 14</code> (hex from 10)</td></tr>
    <tr><td><strong>Alphabetic</strong></td><td>start, step, uppercase</td><td><code>a, b, c … z, aa, ab</code></td></tr>
    <tr><td><strong>Roman</strong></td><td>start, step, uppercase</td><td><code>I, II, III, IV</code></td></tr>
    <tr><td><strong>Dates</strong></td><td>start date, step amount, unit, format</td><td><code>2024-01-01, 2024-01-02</code></td></tr>
    <tr><td><strong>Template</strong></td><td>start, step, template string</td><td><code>row-001, row-002</code></td></tr>
  </table>

  <h2>Template tokens</h2>
  <p>Use <code>{n}</code> in a template as the counter placeholder:</p>
  <table>
    <tr><th>Token</th><th>Result (value 10)</th></tr>
    <tr><td><code>{n}</code></td><td><code>10</code></td></tr>
    <tr><td><code>{n:03}</code></td><td><code>010</code> (zero-padded width)</td></tr>
    <tr><td><code>{n:hex}</code> / <code>{n:HEX}</code></td><td><code>a</code> / <code>A</code></td></tr>
    <tr><td><code>{n:oct}</code>, <code>{n:bin}</code></td><td><code>12</code>, <code>1010</code></td></tr>
    <tr><td><code>{n:alpha}</code> / <code>{n:ALPHA}</code></td><td><code>j</code> / <code>J</code></td></tr>
    <tr><td><code>{n:roman}</code> / <code>{n:ROMAN}</code></td><td><code>x</code> / <code>X</code></td></tr>
  </table>

  <div class="example">Templates:
  item_{n}       →  item_1, item_2, item_3
  row-{n:03}     →  row-001, row-002
  0x{n:hex}      →  0xa, 0xb, 0xc</div>

  <h2>Tips</h2>
  <div class="tip"><strong>Quick repeat:</strong> Run the command again to reuse your last pattern — just confirm each prompt.</div>
  <div class="tip"><strong>Descending:</strong> Use a negative step (e.g. start 10, step -1 → 10, 9, 8).</div>
  <div class="tip"><strong>Legacy behavior:</strong> Choose <strong>Numeric</strong>, start <code>1</code>, step <code>1</code> for the original auto-increment.</div>

  <footer>Format Incrementer 2.0 — by rsunder10 (RedEyeGuy)</footer>
</body>
</html>`;
}

let helpPanel: vscode.WebviewPanel | undefined;

export function showHelp(context: vscode.ExtensionContext): void {
  const column = vscode.window.activeTextEditor?.viewColumn ?? vscode.ViewColumn.One;

  if (helpPanel) {
    helpPanel.reveal(column);
    return;
  }

  helpPanel = vscode.window.createWebviewPanel(
    "formatIncrementerHelp",
    "Format Incrementer Help",
    column,
    { enableScripts: false, retainContextWhenHidden: true }
  );

  helpPanel.webview.html = getHelpHtml();

  helpPanel.onDidDispose(() => {
    helpPanel = undefined;
  });
}
