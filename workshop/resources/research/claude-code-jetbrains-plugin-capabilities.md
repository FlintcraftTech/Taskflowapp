# What the Claude Code plugin for Android Studio can and cannot do

Read 2026-09-05, during a /next run, after the user installed the plugin and asked whether it changes
how Taskflow's build and test work is done.

## What was asked

Taskflow has several `[user]` work items that exist for one reason: Gradle will not run from Claude's
shell on this machine, so compiling and running tests is Android Studio's job and Android Studio is the
user's (TOOLS.md, re-tested 2026-09-05). The question is whether the plugin removes that constraint —
can Claude now run a Gradle task, build the app, or run the instrumentation tests through the IDE?

## What was found

**No. The plugin exposes no code-execution tool to the model.** Anthropic's own documentation states
this outright, in the section describing the local MCP server the plugin runs: exactly one tool reaches
the model, `mcp__ide__getDiagnostics`, and it is read-only. Everything else the server hosts is internal
RPC the CLI uses to draw its own UI — opening diffs, reading the editor selection — and is filtered out
before the tool list reaches Claude. So the plugin does not let Claude press Run, invoke a Gradle task,
or start a test run.

**What it does give.** Quick launch from the editor; diffs rendered in the IDE's diff viewer rather than
the terminal; the current selection and open file passed as context automatically; file-reference
shortcuts; and — the one that matters here — Claude reading the IDE's own inspection diagnostics, the
errors and warnings shown in the editor.

**The diagnostics tool is worth something on its own.** The compile errors that stopped this session's
test run — three call sites in `TaskDaoTest.kt` passing two arguments to a three-argument method — are
exactly what `getDiagnostics` returns. Reading them would not have needed the user to run anything and
paste a screenshot back. It does not replace a build; it does mean a whole class of "this does not
compile" can be seen without one.

**How it is wired, which explains the limit.** The plugin does not bundle the CLI. It runs the `claude`
command in the IDE's integrated terminal and connects to it over a local MCP server on an ephemeral
loopback port, authenticated with a token written to `~/.claude/ide/<port>.lock` at IDE start. The IDE
is a client of Claude Code, not a set of hands Claude can drive.

**One question this does not answer, and it is testable.** A `claude` session started from Android
Studio's *integrated terminal* is a different shell from the one this project's sessions run in.
TOOLS.md records the Gradle loopback failure as specific to the JVM process Gradle forks from Claude's
shell, and records separately that Android Studio itself builds fine on this machine. Whether `gradlew`
succeeds from the IDE's integrated terminal is unknown — nobody has tried it. If it does, the `[user]`
flavor on the test-running and build items may be removable, but on the basis of that shell rather than
of anything the plugin exposes.

## Frame assessment

- **Time range.** Not applicable — a capability question about current software, not a claim about a
  period. Its horizon is the plugin's version: it ships as a beta and is on automatic updates by
  default, so the tool list can change.
- **People.** Applies to this project's own working arrangement — the user on Windows with Android
  Studio, and Claude in a separate shell. Nothing here bears on Taskflow's own users.
- **Freshness.** Amended on a cycle. The plugin is explicitly labelled beta in the marketplace and in
  the docs, and the no-execution-tool line is a design statement that a later release could change.
  Re-read before concluding a second time that Claude cannot run a build through it.
- **Risk if wrong.** Low and self-correcting in one direction, higher in the other. Being wrong that it
  cannot execute code costs the project some unnecessary `[user]` walkthroughs, which is the status quo.
  Being wrong that it *can* would mean writing items that assume Claude can build, which fail at the
  first run — so the conservative reading is also the safe one. No red flag.
- **Alternatives.** The integrated-terminal question above is the untested alternative route and is
  named rather than resolved. Not researched: whether an MCP server for Gradle exists, or whether the
  Android Studio remote-development path changes the shell — neither was needed to answer what was
  asked.

Sources: [JetBrains IDEs — Claude Code docs](https://code.claude.com/docs/en/jetbrains),
[Claude Code plugin listing, JetBrains Marketplace](https://plugins.jetbrains.com/plugin/27310-claude-code-beta-)
