package com.rsunder10.formatincrementer

/**
 * HTML help shown in the in-IDE help dialog. Keep in sync with
 * [vscode/src/help.ts] and [docs/HELP.md].
 */
object HelpContent {

    const val SHORTCUT_MAC = "Shift + Cmd + 1, then Shift + Cmd + 1"
    const val SHORTCUT_WIN = "Shift + Ctrl + 1, then Shift + Ctrl + 1"

    fun html(): String = """
        <html>
        <head>
          <style>
            body { font-family: -apple-system, Segoe UI, sans-serif; font-size: 13px; line-height: 1.5; color: #2b2b2b; margin: 0; padding: 0 4px 12px; }
            h1 { font-size: 20px; font-weight: 600; margin: 0 0 4px; color: #1a1a1a; }
            h2 { font-size: 15px; font-weight: 600; margin: 20px 0 8px; color: #1a1a1a; border-bottom: 1px solid #e0e0e0; padding-bottom: 4px; }
            h3 { font-size: 13px; font-weight: 600; margin: 14px 0 6px; color: #333; }
            p { margin: 6px 0; }
            ul, ol { margin: 6px 0 6px 18px; padding: 0; }
            li { margin: 4px 0; }
            code { font-family: JetBrains Mono, Consolas, monospace; font-size: 12px; background: #f4f4f5; padding: 1px 5px; border-radius: 3px; }
            kbd { font-family: JetBrains Mono, Consolas, monospace; font-size: 11px; background: #fafafa; border: 1px solid #ccc; border-bottom-width: 2px; padding: 1px 6px; border-radius: 4px; }
            table { border-collapse: collapse; width: 100%; margin: 8px 0; font-size: 12px; }
            th { text-align: left; background: #f4f4f5; padding: 6px 8px; border: 1px solid #e0e0e0; font-weight: 600; }
            td { padding: 6px 8px; border: 1px solid #e0e0e0; vertical-align: top; }
            .lead { font-size: 14px; color: #555; margin-bottom: 12px; }
            .tip { background: #f0f7ff; border-left: 3px solid #3574f0; padding: 8px 12px; margin: 10px 0; border-radius: 0 4px 4px 0; }
            .example { background: #f9f9f9; border: 1px solid #e8e8e8; padding: 10px 12px; border-radius: 6px; font-family: JetBrains Mono, Consolas, monospace; font-size: 12px; white-space: pre; margin: 8px 0; }
            .shortcut { display: inline-block; background: #eef2ff; border: 1px solid #c7d2fe; padding: 4px 10px; border-radius: 6px; font-size: 12px; margin: 4px 0; }
          </style>
        </head>
        <body>
          <h1>Format Incrementer</h1>
          <p class="lead">Fill every caret or selection with a generated sequence &mdash; numbers, letters, dates, Roman numerals, hex, or your own template.</p>

          <h2>Quick start</h2>
          <ol>
            <li><b>Place multiple carets</b> &mdash; hold <kbd>Alt</kbd> (Option on Mac) and click, or make multiple selections.</li>
            <li><b>Run the command</b> &mdash; Tools &rarr; <b>Format Incrementer...</b>, right-click in the editor, or press the shortcut below.</li>
            <li><b>Pick a pattern</b> &mdash; choose a type, set start/step (or other fields), check the live preview, then click <b>Apply</b>.</li>
          </ol>

          <h2>Keyboard shortcut</h2>
          <p>The same chord is used in IntelliJ and VS Code:</p>
          <p class="shortcut"><b>macOS:</b> $SHORTCUT_MAC</p>
          <p class="shortcut"><b>Windows / Linux:</b> $SHORTCUT_WIN</p>
          <p>Press the combination <b>twice in a row</b> (a chord shortcut).</p>

          <h2>Where to find it</h2>
          <table>
            <tr><th>Location</th><th>Action</th></tr>
            <tr><td>Tools menu</td><td><b>Format Incrementer...</b></td></tr>
            <tr><td>Editor right-click</td><td><b>Format Incrementer...</b></td></tr>
            <tr><td>Help</td><td><b>Format Incrementer Help</b> (this guide)</td></tr>
          </table>

          <h2>How carets are filled</h2>
          <ul>
            <li>Carets and selections are processed in <b>document order</b> (top to bottom, left to right).</li>
            <li>The first caret gets index 0, the second index 1, and so on.</li>
            <li>Empty carets <b>insert</b> text; selections are <b>replaced</b>.</li>
            <li>All changes happen in a <b>single undo step</b> &mdash; press Undo once to revert everything.</li>
            <li>Your last-used pattern is remembered for the next run.</li>
          </ul>

          <div class="example">Example &mdash; numeric, start 1, step 1:

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
            <tr><td><b>Numeric</b></td><td>start, step</td><td><code>1, 3, 5</code> (start 1, step 2)</td></tr>
            <tr><td><b>Zero-padded</b></td><td>start, step, width, pad char</td><td><code>001, 002, 003</code></td></tr>
            <tr><td><b>Radix</b></td><td>start, step, base (2&ndash;36), uppercase</td><td><code>a, f, 14</code> (hex from 10)</td></tr>
            <tr><td><b>Alphabetic</b></td><td>start, step, uppercase</td><td><code>a, b, c &hellip; z, aa, ab</code></td></tr>
            <tr><td><b>Roman</b></td><td>start, step, uppercase</td><td><code>I, II, III, IV</code></td></tr>
            <tr><td><b>Dates</b></td><td>start date, step amount, unit, format</td><td><code>2024-01-01, 2024-01-02</code></td></tr>
            <tr><td><b>Template</b></td><td>start, step, template string</td><td><code>row-001, row-002</code></td></tr>
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
  item_{n}       &rarr;  item_1, item_2, item_3
  row-{n:03}     &rarr;  row-001, row-002
  0x{n:hex}      &rarr;  0xa, 0xb, 0xc</div>

          <h2>Tips</h2>
          <div class="tip"><b>Quick repeat:</b> Press the shortcut again to reuse your last pattern &mdash; just hit Apply in the dialog.</div>
          <div class="tip"><b>Descending:</b> Use a negative step (e.g. start 10, step -1 &rarr; 10, 9, 8).</div>
          <div class="tip"><b>Legacy behavior:</b> Choose <b>Numeric</b>, start <code>1</code>, step <code>1</code> for the original auto-increment.</div>

          <p style="margin-top: 20px; color: #888; font-size: 11px;">Format Incrementer 2.0 &mdash; by rsunder10 (RedEyeGuy)</p>
        </body>
        </html>
    """.trimIndent()
}
