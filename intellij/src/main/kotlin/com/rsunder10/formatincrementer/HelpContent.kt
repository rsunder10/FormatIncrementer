package com.rsunder10.formatincrementer

/**
 * HTML help shown in the in-IDE help dialog.
 *
 * Uses only HTML/CSS that Swing's [javax.swing.JEditorPane] renderer supports (HTML 3.2
 * subset). Modern CSS such as border-radius, display, or border-bottom-width crashes
 * the parser with NPE. Keep in sync with [vscode/src/help.ts] and [docs/HELP.md].
 */
object HelpContent {

    const val SHORTCUT_MAC = "Shift + Cmd + 1"
    const val SHORTCUT_WIN = "Shift + Ctrl + 1"

    fun html(): String = """
        <html>
        <body style="font-family: sans-serif; font-size: 12pt; margin: 8px;">
          <h1>Format Incrementer</h1>
          <p>Fill every caret or selection with a generated sequence &mdash; numbers, letters,
          dates, Roman numerals, hex, or your own template.</p>

          <h2>Quick start</h2>
          <ol>
            <li><b>Place multiple carets</b> &mdash; hold <b>Alt</b> (Option on Mac) and click,
            or make multiple selections.</li>
            <li><b>Run the command</b> &mdash; Tools &rarr; <b>Format Incrementer...</b>,
            right-click in the editor, or press the shortcut below.</li>
            <li><b>Pick a pattern</b> &mdash; choose a type, set start/step (or other fields),
            check the live preview, then click <b>Apply</b>.</li>
          </ol>

          <h2>Keyboard shortcut</h2>
          <p>The same shortcut is used in IntelliJ and VS Code:</p>
          <ul>
            <li><b>macOS:</b> $SHORTCUT_MAC</li>
            <li><b>Windows / Linux:</b> $SHORTCUT_WIN</li>
          </ul>

          <h2>Where to find it</h2>
          <table border="1" cellpadding="4" cellspacing="0" width="100%">
            <tr><th align="left">Location</th><th align="left">Action</th></tr>
            <tr><td>Tools menu</td><td><b>Format Incrementer...</b></td></tr>
            <tr><td>Editor right-click</td><td><b>Format Incrementer...</b></td></tr>
            <tr><td>Help</td><td><b>Format Incrementer Help</b> (this guide)</td></tr>
          </table>

          <h2>How carets are filled</h2>
          <ul>
            <li>Carets and selections are processed in <b>document order</b>
            (top to bottom, left to right).</li>
            <li>The first caret gets index 0, the second index 1, and so on.</li>
            <li>Empty carets <b>insert</b> text; selections are <b>replaced</b>.</li>
            <li>All changes happen in a <b>single undo step</b> &mdash; press Undo once
            to revert everything.</li>
            <li>Your last-used pattern is remembered for the next run.</li>
          </ul>

          <h3>Example (numeric, start 1, step 1)</h3>
          <pre>
Before:                 After:
line |one|              line 1
line |two|              line 2
line |three|            line 3
          </pre>

          <h2>Pattern reference</h2>
          <table border="1" cellpadding="4" cellspacing="0" width="100%">
            <tr>
              <th align="left">Pattern</th>
              <th align="left">Fields</th>
              <th align="left">Example output</th>
            </tr>
            <tr><td><b>Numeric</b></td><td>start, step</td><td>1, 3, 5 (start 1, step 2)</td></tr>
            <tr><td><b>Zero-padded</b></td><td>start, step, width, pad char</td><td>001, 002, 003</td></tr>
            <tr><td><b>Radix</b></td><td>start, step, base (2-36), uppercase</td><td>a, f, 14 (hex from 10)</td></tr>
            <tr><td><b>Alphabetic</b></td><td>start, step, uppercase</td><td>a, b, c ... z, aa, ab</td></tr>
            <tr><td><b>Roman</b></td><td>start, step, uppercase</td><td>I, II, III, IV</td></tr>
            <tr><td><b>Dates</b></td><td>start date, step amount, unit, format</td><td>2024-01-01, 2024-01-02</td></tr>
            <tr><td><b>Template</b></td><td>start, step, template string</td><td>row-001, row-002</td></tr>
          </table>

          <h2>Template tokens</h2>
          <p>Use <code>{n}</code> in a template as the counter placeholder:</p>
          <table border="1" cellpadding="4" cellspacing="0" width="100%">
            <tr><th align="left">Token</th><th align="left">Result (value 10)</th></tr>
            <tr><td><code>{n}</code></td><td>10</td></tr>
            <tr><td><code>{n:03}</code></td><td>010 (zero-padded width)</td></tr>
            <tr><td><code>{n:hex}</code> / <code>{n:HEX}</code></td><td>a / A</td></tr>
            <tr><td><code>{n:oct}</code>, <code>{n:bin}</code></td><td>12, 1010</td></tr>
            <tr><td><code>{n:alpha}</code> / <code>{n:ALPHA}</code></td><td>j / J</td></tr>
            <tr><td><code>{n:roman}</code> / <code>{n:ROMAN}</code></td><td>x / X</td></tr>
          </table>

          <h3>Template examples</h3>
          <pre>
item_{n}    -&gt;  item_1, item_2, item_3
row-{n:03}  -&gt;  row-001, row-002
0x{n:hex}   -&gt;  0xa, 0xb, 0xc
          </pre>

          <h2>Tips</h2>
          <ul>
            <li><b>Quick repeat:</b> Press the shortcut again to reuse your last pattern
            &mdash; just hit Apply in the dialog.</li>
            <li><b>Descending:</b> Use a negative step (e.g. start 10, step -1 gives 10, 9, 8).</li>
            <li><b>Legacy behavior:</b> Choose <b>Numeric</b>, start 1, step 1 for the
            original auto-increment.</li>
          </ul>

          <p><i>Format Incrementer 2.0 &mdash; by rsunder10 (RedEyeGuy)</i></p>
        </body>
        </html>
    """.trimIndent()
}
