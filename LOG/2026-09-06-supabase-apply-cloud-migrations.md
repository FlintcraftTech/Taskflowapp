# 6d6267c — /next [supabase-apply-cloud-migrations]: the migrations applied, the isolation proved, and a missing-grants defect found by the test failing

**Outcome: done.** Walked to its end on 2026-09-06, all nine steps. The four tables and sixteen policies now
exist on the real Supabase project; one account cannot read another's rows; a session-less request is denied,
and so is a signed-in request carrying no identity. Two defects came out of the drive rather than out of
reading — [cloud-schema-missing-grants] and [rls-test-steps-bypass-rls] — and the first of them would have
stopped the app dead on its first real query.

Original heading: driving the cloud migrations and the cross-account read test

Recorded 2026-09-06, from 11:42, during a /next run. Written live as the walk-through proceeds.

Both SQL files the item waits on exist in the repository, checked at the start of the drive:
`supabase/migrations/0001_initial_schema.sql` (4,234 bytes) and `0002_rls_policies.sql` (4,406 bytes),
both written on 2026-09-05.

The capability check was re-run before handing the first step over, and the item's tagging holds. There is
still no Supabase CLI on this machine, and the two routes to a Supabase project both need a credential
Claude is barred from handling — the CLI wants a project access token, the dashboard wants Alex's Google
sign-in. A browser is reachable from this session, which does not change the answer: reaching the
dashboard through it would still mean Alex's sign-in going through Claude.

## Steps as they happen

- Step 1 handed over 2026-09-06, 11:42: open the Supabase dashboard, select the Taskflow project, open the
  SQL editor. **Done.** Alex was on the dashboard but could not find the editor, so the route was verified
  from Supabase's own documentation rather than described from memory — the docs give
  `https://supabase.com/dashboard/project/_/sql`, where `_` resolves to the open project. That worked: she
  reached the editor in the Taskflow project under the FlintCraft organisation, on the `main` branch marked
  PRODUCTION, with an empty query pane and a Run button. Worth carrying forward as the route for any later
  session that has to send someone to this editor.
- Step 2 handed over 2026-09-06, 11:48: paste and run `0001_initial_schema.sql`. **Done** — the editor
  returned "Success. No rows returned", which is what a run of pure DDL returns.
- Step 3 handed over 2026-09-06, 11:52: confirm Row Level Security is on for all four tables. The item's
  own wording sends the user to the table editor to look for an RLS marker against each table. That was
  replaced with a query against `pg_tables` run in the pane already open, because it answers the same
  question as four rows of true/false rather than as a hunt through a UI this session cannot see, and it
  keeps the user on one surface. The claim checked is unchanged. **Passed** — four rows, `life_areas`,
  `projects`, `strategy_entries` and `tasks`, every one with `rowsecurity` true. All four, not some.
- Step 4 handed over 2026-09-06, 11:56: paste and run `0002_rls_policies.sql`, sixteen policies. **Done** —
  "Success. No rows returned". The policies now exist on the database rather than only in this repository,
  which was the first half of what this item is for.
- **Steps 5 to 7 were rewritten before being handed over, and the item's own versions are wrong.** Checked
  against Supabase's Row Level Security documentation on 2026-09-06: the SQL editor connects as `postgres`,
  which has `bypassrls`, so the item's "signed in as the first test user, query tasks" returns every row
  whatever the policies say. A run following the item would have produced a result indistinguishable from a
  broken policy. The documented route is to impersonate inside the transaction — `set local role
  authenticated` with `set local request.jwt.claim.sub`, and `set local role anon` for the null-identity
  case. Two smaller faults came out of the same reading: `tasks.project_id` is not-null and references
  `projects`, so step 5 cannot insert a task without a project first; and a missing grant raises `42501`
  before any policy runs, which is a different failure from a denial and should not be read as one. Filed as
  [rls-test-steps-bypass-rls] so the queue entry gets fixed; what follows here is what was actually driven.
- Step 5 handed over 2026-09-06, 12:06: create two test users in the project's authentication section. The
  impersonation route needs only their ids, so their passwords are never handled by this session and are
  never asked for. **Done** — `test-one@example.com` and `test-two@example.com` both created, each with
  its own UID. Two navigation facts worth keeping: the `project/_/` shorthand that worked for `/sql` did
  **not** resolve for `/auth/users` and bounced to the project picker, so the shorthand is per-path rather
  than general; and the users page's own control is labelled "Add user".
- Step 6 handed over 2026-09-06, 12:14: seed one project and one task per test user. Written to look the
  users up by email rather than by id, so that no UUID has to be read off a screen and retyped — Alex
  scores low on symbol search, and a mistyped UUID here would produce a foreign-key error that looks like
  a schema fault. **Done** — two rows came back, one `RLS test task` against each test email. That query ran
  as `postgres`, so seeing both is the baseline the isolation test is measured against rather than a result
  in itself.
- Step 7, first attempt, 12:18: **failed with `42501: permission denied for table tasks`**, and the failure
  is the session's most important finding. The migrations create the tables, enable RLS and write sixteen
  policies, and never grant the `authenticated` role a single privilege. Postgres checks privilege before
  policy, so the app's own first query would have hit this same wall. Filed as [cloud-schema-missing-grants].
  It fails closed — nothing is exposed, the tables are simply unreachable — and that is stated in the
  capture so "missing grants" is not misread as a hole. Nothing had caught it because every prior check ran
  in the SQL editor as `postgres`, which owns the tables and bypasses RLS.
- Grants applied live on Alex's approval, 12:24, after being shown the four statements and told plainly
  that this changes the live database rather than reading it: `grant select, insert, update, delete` on each
  of the four tables to `authenticated`. This is what the database's own error hint asked for and what the
  app will need regardless; where the grants should permanently live is left to [cloud-schema-missing-grants]
  rather than decided mid-drive.
- Step 7, second attempt, 12:26: **PASSED.** Impersonating `test-one@example.com` returned exactly one row,
  with `user_id` `fe208208-703b-400c-a9a1-02efc3573706`, matching that user's own id. The second test user's
  task was invisible. This is the cross-account read denial the whole item exists to prove.
- Step 8, the null-identity case, 12:29: **PASSED, and split into two checks.** As `anon` the query failed
  with `42501` — denied at the grant, one gate before any policy, since the grants above named only
  `authenticated`. That is a stronger denial than the item asked for, but it left the policies' own
  null-identity wording unexercised. So a second query ran as `authenticated` with no JWT claim set, where
  the grant permits the query and `auth.uid()` is null: it returned "Success. No rows returned". The policy
  layer denies the null identity on its own, which is what the `is not null` conjunction in `0002` was
  written for. Both belts hold, and they were tested separately rather than one being taken for the other.
- Step 9, 12:32: delete the two test users, which cascades their project and task rows away. **Done** —
  `tasks_left` 0 and `projects_left` 0. The project is left holding the schema, the policies and the grants,
  and none of the test data.

## What the next session should know

The grants are on the database but in no file. Anyone re-creating this project from
`supabase/migrations/` alone gets the broken state this drive found, until
[cloud-schema-missing-grants] is built. That is the one gap between the repository and the live project.

The test emails recorded here use `example.com`, the reserved documentation domain, so they name no real
address. The secret-shape check flags them on every write to this file; they are left as they are because
the exact strings are what a later session would need to re-run the drive.
