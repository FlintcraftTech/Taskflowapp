# [HASH] — build [claude-md-stale-vocabulary]: the four stale Project-rules references in CLAUDE.md rewritten to current vocabulary

The plugin-managed half of this item had already landed in the 2026-08-25 /setup run; this build did the remaining half — the four stale references in the user-owned Project rules section of CLAUDE.md. The archived-backlog-specs paragraph now points at each work item's summary in QUEUE.md instead of "QUEUE.md's Batches"; the migration note records REGISTRY.md as deleted rather than listing it as a live doc; the TEST-LOG.md rule now reads one row per test, per shipped work item, with the "test batch" wording gone; and the SYSTEM-PROMPT.md rule keeps the `Serves SYSTEM-PROMPT.md:` convention, reattached to a work item rather than a batch. Confirmed by grep at build time: no "batch", "Deferred tests", "Parked:" or "Plan session here" remain, and the one REGISTRY.md mention states its deletion.

This entry was written at the close of the build by a later session: the building session crashed before running /done, and its build working file carried the record this entry is authored from. The reasoning here is that file's, not this session's memory.

**Changes:** CLAUDE.md — four references rewritten in the user-owned Project rules section; plugin-managed block untouched.

**Tests:** none to run — a documentation change, verified by grep.
