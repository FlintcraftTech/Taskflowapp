# CLAUDE.md

<!-- ▼ PLUGIN-MANAGED — do not edit between these markers. Refreshed by /setup's migration, which reports what it replaces. Your own rules belong below the end marker. ▼ -->

This project uses the Throughliner method.

## Project docs

- **SPEC.md** — product truth. What it is, who it's for, how it works.
- **QUEUE.md** — your work, in two sections. **Processed** work is vetted and ready to build, worked top-to-bottom; a `--- Cleared to run above this line ---` line marks how far down is greenlit (below it is decided but not ready yet). **Unprocessed** work is captured ideas and tasks not yet fully processed. Each piece of work is one line: a `#### ` heading naming the work, with a `[slug]` at the end of that heading line and a short rationale beneath it, plus a `captured by you` credit on items you personally raised (anything else is unmarked — Claude is the default author). A work item can carry a leading flavor tag: none means a build (Claude edits files), `[audit]` a review pass (Claude reads and reports), `[user]` a step only you can run. A security or privacy risk Claude surfaces becomes a work item carrying a `Red flag · State: cleared/uncleared` marker — surfaced first each session while uncleared, until it's cleared (either designed out, or you're told the risk plainly and choose to accept it).
- **LOG/** — session records: what was built, tested, decided. One file per session entry, plus index.md one-line summaries naming each entry file.
- **FAQ/** — workflow FAQ. Index loaded at session start; details in FAQ/faq.md.
- **INBOX/** — messages from other projects you run. Anything waiting is mentioned at session start; handled messages move to `INBOX/archive/`. A message going out to another project is always shown to you for approval first.

## Workflow

- `/setup` — scaffold project docs (done if you're reading this).
- `/plan` — queue management, captures, design questions.
- `/next` — execute the top piece of ready work (a build or an audit, by its flavor tag). It can work several cleared pieces of work back-to-back, top-down, stopping at the readiness line or when something genuinely needs you.
- `/rescan` — read back over the conversation and file anything decided or noticed but never written down. Run it whenever, as often as you like; it only looks back as far as the last time you ran it. It files things and leaves the deciding to /plan.
- `/done` — record, update docs, commit.

## Rules for Claude

- SPEC.md is a normal doc, and there's no separate spec-edit step — but **it changes during planning, not during a build**. When a planning decision changes what SPEC says, Claude writes that sentence in the /plan session, with you there. A build never writes product truth: if a build discovers SPEC is missing a sentence, it writes the sentence down as a new queue item and carries on, so SPEC is behind by at most that one sentence until your next planning session — and it's behind visibly, as an item you can see, rather than quietly. The reason is that the session which made a choice shouldn't be the one that certifies it as product truth. A large SPEC rework is ordinary build work that lists SPEC.md among its files, and the safety check still blocks a build from editing SPEC unless it does. Note spec issues for /plan as they come up.

## Language

Language: English

<!-- ▲ PLUGIN-MANAGED — do not edit above this line. ▲ -->

## Project rules

**Building on Windows:** this machine hits Gradle's "Unable to delete directory … build" lock (Gradle's file-system watching, and/or Defender, holding handles on `build/`). Build with `--no-watch-fs --no-daemon` — e.g. `.\gradlew.bat :app:assembleDebug --no-watch-fs --no-daemon`. If a stale lock persists, stop the daemon (`.\gradlew.bat --stop`) and delete `app\build`. Verified working 2026-06-16.

**Additional source-of-truth doc — `SYSTEM-PROMPT.md`:** the system prompt the remote MCP server hands Claude on connection (paid tier only). It covers life-area exploration, project-suggestion etiquette, Strategy doc reconciliation, proactive Taskflow checks, and tone. Treat it as locked during builds the same way SPEC.md is — the scope hook does not auto-lock it, so this is a rule to follow, not an enforced one: don't edit it during a build unless it's named in `_build.md`'s Files: list. Planning resolutions that describe SYSTEM-PROMPT.md behaviour fold into it rather than SPEC.md. Build batches that change its domain carry a `Serves SYSTEM-PROMPT.md: ...` line.

**Additional record doc — `TEST-LOG.md`:** a table of test outcomes per shipped build batch, one row per test, maintained by Claude during builds and planning. It predates this project's move to LOG/-based session records; it's kept as the running test history. New per-session test results are recorded in the LOG/ entry; reflect material test outcomes (pass/fail/skip) in TEST-LOG.md too when a build or test batch runs tests.

**Archived backlog specs — `archive/backlog-specs/`:** the detailed per-batch spec files (and the original backlog `INDEX.md`) from before this project adopted the single-file QUEUE.md. The summary of each lives in QUEUE.md's Batches; the archived file is the full original spec. Reference material — not maintained going forward.

**Migration note:** this project was migrated from the method's older document vocabulary (UX.md → SPEC.md, BACKLOG/ → QUEUE.md, MANIFEST.md → REGISTRY.md, build-log/ → LOG/). Docs now live at the project root, where the current plugin's hooks expect them; the old per-project path-block JSON is no longer used.
