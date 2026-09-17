# [HASH] — /plan [run-migration-test-on-emulator]: re-aimed at the upgrade still ahead of Alex's phone rather than the one already behind it, and re-flavoured as an audit

**Kind: processed.** Recorded 2026-09-17 at 16:01, read from the clock, during a planning session.

As filed, this was about running the 5 → 6 migration case that shipped on 2026-09-12 and had never been
run, now that an emulator exists to run it on without wiping Alex's real tasks.

Two things reshaped it. The upgrade it would prove **has already happened to her data**: the build
installed on her phone on 2026-09-12 carries schema 6, so her real database went through that migration
untested, and it evidently survived — she has used the app since. Running it now confirms after the fact.
Meanwhile [roster-as-subtasks], settled in this same session, adds a schema 7 with its own upgrade step and
its own test case, and that is the upgrade still ahead of her phone. So the item moved to sit after that
work, and now covers both migrations in one run — the one that can still cost her something included.

Re-flavoured `[audit]`: it was filed as a build and changes no files. It runs a command and reports what
came back.

Recorded because it will shape how the item is driven: the Gradle run cannot happen in Claude's own shell
on this machine, so it follows the project's compile rule — the run stops, hands Alex the lines to paste
into Android Studio's terminal, and waits. The whole `androidTest` suite runs rather than the migration
cases alone, because the suite was last run in full on 2026-09-05 at schema 5 and two versions have landed
since.

**Queue changes:** [run-migration-test-on-emulator] rewritten, re-flavoured `[audit]`, and cleared to run,
placed after [strategy-paragraph-input-loses-characters] so the schema work precedes it. The ordering is
written into [roster-as-subtasks] as well.

**Work processed:** kept — [run-migration-test-on-emulator].
