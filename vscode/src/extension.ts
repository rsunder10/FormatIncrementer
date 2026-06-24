import * as vscode from "vscode";
import { showHelp } from "./help";
import { DEFAULT_CONFIG, PatternConfig, render } from "./sequence";
import { pickPattern } from "./ui";

const STATE_KEY = "formatIncrementer.lastConfig";

export function activate(context: vscode.ExtensionContext): void {
  context.subscriptions.push(
    vscode.commands.registerCommand("formatIncrementer.increment", () =>
      runIncrement(context)
    ),
    vscode.commands.registerCommand("formatIncrementer.showHelp", () =>
      showHelp(context)
    )
  );
}

export function deactivate(): void {
  // no-op
}

async function runIncrement(context: vscode.ExtensionContext): Promise<void> {
  const editor = vscode.window.activeTextEditor;
  if (!editor) {
    vscode.window.showInformationMessage("Format Incrementer: open an editor first.");
    return;
  }

  const initial = loadConfig(context);
  const config = await pickPattern(initial);
  if (!config) {
    return;
  }
  saveConfig(context, config);

  // Order selections by document position so the sequence follows reading order.
  const selections = [...editor.selections].sort((a, b) =>
    a.start.compareTo(b.start)
  );

  await editor.edit((builder) => {
    selections.forEach((selection, i) => {
      const text = render(config, i);
      if (selection.isEmpty) {
        builder.insert(selection.active, text);
      } else {
        builder.replace(selection, text);
      }
    });
  });
}

function loadConfig(context: vscode.ExtensionContext): PatternConfig {
  const stored = context.globalState.get<Partial<PatternConfig>>(STATE_KEY);
  return { ...DEFAULT_CONFIG, ...(stored ?? {}) };
}

function saveConfig(context: vscode.ExtensionContext, config: PatternConfig): void {
  void context.globalState.update(STATE_KEY, config);
}
