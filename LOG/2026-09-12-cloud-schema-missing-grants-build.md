# bbbef3c — /next [cloud-schema-missing-grants]: 0003_grants.sql written, closing the gap between the repository and the live Supabase project

Written 2026-09-12 at 13:05, read from the clock.

Row Level Security and table privileges are two separate gates, and Postgres checks the privilege first. A policy says which rows a role may touch; it does not give the role permission to touch the table at all. `0001_initial_schema.sql` creates the four tables and turns RLS on but issues no `grant`; `0002_rls_policies.sql` creates sixteen policies scoped `to authenticated` and grants nothing. So with the migrations exactly as they stood, the first real query from a signed-in Taskflow user failed before any policy was consulted — observed on 2026-09-06 as `ERROR: 42501: permission denied for table tasks`.

This fails closed, so it is a functionality defect and not a data-exposure risk. Nothing was readable that should not have been; the tables were simply unreachable. Worth stating plainly, because the words "missing grants" read like a hole and here the hole is in the other direction.

The build wrote the file the 2026-09-07 planning session designed, and its header carries the four decisions so they are not re-opened. **A new migration rather than an edit to `0001` or `0002`**, because both have already been applied to the live project and editing an applied migration makes the file stop describing what actually ran. **Per-table grants written out; refused: `alter default privileges` on the schema**, which reaches only tables created after it is set, so it would do nothing for four existing tables while silently opening future ones — the opposite of the closed-by-default posture `0001` takes. **`grant` is idempotent**, which is what makes this file a repair for the live database rather than a change to it: the grants are already there by hand from the 2026-09-06 drive, so applying it proves the file and the project agree. **Refused: `if not exists` guards on the applied files**, since a migration series is applied once in order and making a re-run safe invites one.

Why it was not caught earlier: the SQL editor connects as `postgres`, which holds `bypassrls` and owns the tables, so every check run there succeeds regardless. Nothing had queried these tables as the role the app will actually use until that drive.

**Files touched:** new `supabase/migrations/0003_grants.sql`, 41 lines — four `grant select, insert, update, delete … to authenticated` statements and nothing to `anon`, matching `0002`'s stance that a request with no session matches no policy at all. No sequence grants: every primary key is a UUID default. Reads but does not change `0001_initial_schema.sql` and `0002_rls_policies.sql`.

**Routed to Captures:** [supabase-apply-0003-grants] — the item's own observation names a live-database check, and applying a migration to Alex's production project is hers to authorise rather than something a run presses the button on. Nothing in the queue was tracking it.

**Verification:** done, UNCONFIRMED — the file has not been applied to the live Supabase project, and no select as the `authenticated` role has been run against the four tables. That check is [supabase-apply-0003-grants].
