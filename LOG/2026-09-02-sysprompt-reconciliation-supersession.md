# [HASH] — build [sysprompt-reconciliation-supersession]: a new Strategy edit supersedes a reconciliation still waiting on an answer

`SYSTEM-PROMPT.md` described Strategy-doc reconciliation without saying what happens when the user submits a new edit while a previous pass's suggestions are still unanswered. The rule was decided in planning on 2026-06-16 — the new edit wins — and this item existed to write it into the doc so that [0021-strategy-doc-reconciliation-paid-tier] is built against a complete description rather than an implicit one.

The passage added to §Strategy doc reconciliation → *Ongoing reconciliation* says the newer edit triggers fresh reconciliation against the latest version and **folds in** the earlier pass's outstanding suggestions rather than stacking the two, and keeps the reason in the doc: suggestions written against a version the user has already moved past confuse rather than help, and the newest text is the source of truth about what they want.

The reason is kept in the doc deliberately. This file ships basically verbatim as the system prompt Claude receives on connection, so a rule without its reason is a rule Claude has to guess the edges of.

`SYSTEM-PROMPT.md` is treated as locked during a build in the same way SPEC is, unless the item names it — this one does, which is what made the edit legitimate here rather than something to file.

**Files touched:** SYSTEM-PROMPT.md — §Strategy doc reconciliation → *Ongoing reconciliation* gains the supersession rule and one line of rationale.

**Routed to Captures:** none.

**Tick:** done, confirmed: the Ongoing reconciliation passage now states the supersession rule and its reason; read back after writing.
