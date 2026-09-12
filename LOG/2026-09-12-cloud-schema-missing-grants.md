# d6ac7e8 — /plan [cloud-schema-missing-grants]: kept and cleared, with its three open questions settled — a new 0003 grants file, per-table, and no guards added to the migrations that already ran

The cloud migrations create four tables and enable Row Level Security on each, and
write sixteen policies — but grant the signed-in role no privilege on any table.
Privileges and policies are separate gates and Postgres checks the privilege first,
so a real user's first query fails before any policy is consulted. It was observed
rather than reasoned: an impersonated select returned `permission denied for table
tasks` with the database's own hint naming the fix. It fails closed, so it is a
functionality defect and not exposure.

Three questions the capture left open, all settled after reading both migration
files:

- A new `0003_grants.sql` rather than an edit to `0001` or `0002`. Both have been
  applied to the live project; editing an applied migration makes the file stop
  describing what ran, and a fresh project would then be built by a sequence
  nobody has exercised.
- Per-table grants written out. Refused: `alter default privileges` on the schema —
  it reaches only tables created after it is set, so it would do nothing for four
  that already exist, and it would silently open future ones, which is the opposite
  of the closed-by-default posture `0001` takes.
- `grant` is idempotent, so `0003` is safe to re-run — which is also what makes it
  the repair for the live database rather than a change to it, since the grants are
  already there by hand from the 2026-09-06 drive.

Refused: adding `if not exists` guards to `0001` and `0002`. A migration series is
applied once in order, so re-running an applied file is not something to make safe
— doing so invites it.

**Queue changes:** kept into Processed, cleared to run, with the four decisions,
its file list and its observation written into its own prose.

**Work processed:** kept — [cloud-schema-missing-grants].
