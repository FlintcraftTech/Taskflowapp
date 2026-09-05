# supabase/

The SQL that creates Taskflow's cloud tables and the rules that keep each
account's rows reachable only by that account. Nothing here runs by itself.

## The two files

- **`migrations/0001_initial_schema.sql`** creates the four cloud tables —
  `projects`, `tasks`, `strategy_entries`, `life_areas` — mirroring the local
  Room schema at version 5. Each table has a UUID primary key rather than the
  local autoincrementing integer, because two devices on one account would both
  generate local id 1. Each carries a `user_id` naming the account that owns the
  row, and each has Row Level Security turned on.
- **`migrations/0002_rls_policies.sql`** creates the policies: select, insert,
  update and delete on each of the four tables, every one of them matching
  `user_id` against the account the request's token belongs to.

Between the two files the tables exist and are closed to everyone, including
Taskflow itself. That is deliberate — a table is never briefly readable while
its rules are still being written.

## Why the policies carry the whole weight

The key the Android app uses to reach Supabase ships inside the app, on every
phone. It is public by construction and cannot be treated as a secret. What
stops one phone's key reading another account's tasks is not the key: it is
these policies, checked by the database on every request. A table with the rules
off, or a rule that matches everything, is readable by anyone who unpacks the
app.

Each policy is written so that a request with no signed-in account is denied.
That case is worth stating because it fails quietly: the identity of a caller
with no session is null, and a comparison against null is neither true nor false
— phrased carelessly, as a negation or an "or", it widens to every row instead
of none. The policies use no negation and no "or", and check for the null case
explicitly.

## Applying them

These files are not applied by anything in this repository. Running them against
the real Supabase project, and then proving that one account cannot read
another's rows, is the work item `[supabase-apply-cloud-migrations]` in
`QUEUE.md`.

## A naming note

Supabase renamed its keys: `sb_publishable_` replaces what older documents call
the anon key, and `sb_secret_` replaces the service_role key. Documents and
prompts written earlier in this project still use the old names.
