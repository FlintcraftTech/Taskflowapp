# Supabase RLS, and how an Edge Function reaches rows as the calling user

Read 2026-09-05, during planning of [supabase-rls-policies].

## What was asked

Two things, both bearing on Taskflow's cloud tables. First: is the key that
ships inside the Android app genuinely safe to publish, and what has to be true
for that to hold. Second: can the remote MCP server — planned as a Supabase Edge
Function in [0020-remote-mcp-server] — read a user's rows *through* Row Level
Security, or must it use a privileged key that bypasses RLS entirely.

## What was found

**The publishable key is safe to expose only because RLS is what protects the
rows.** Supabase's own position is that the publishable key may ship in a client
because access is checked against the table's policies and the caller's token,
not against the key. The corollary is the whole point: every table in the public
schema is reachable through the auto-generated API using that key, so a table
with RLS off — or a policy of `using (true)` — is effectively public to anyone
who reads the key out of the APK.

**The per-user policy shape is `user_id = auth.uid()`**, with `auth.uid()`
returning the id of the user the request's token belongs to. A policy is written
per table and per operation, scoped `TO authenticated`.

**One failure mode is worth writing down because it is silent.** For a caller
with no session `auth.uid()` is null, and a policy phrased as a comparison
against null evaluates to null rather than false. Phrased carelessly — as a
negation, or an `or` — that can widen to every row instead of none. The policy
has to be written so the no-session case denies.

**An Edge Function can run as the caller.** Supabase's function auth guide gives
two modes. In user mode the platform validates the caller's JWT before the
handler runs and hands the handler a Supabase client already scoped to that
user, so the same RLS policies apply to the function as to the app — no header
plumbing written by hand. In secret mode the function gets an admin client that
bypasses RLS altogether, which is what cron jobs and privileged maintenance use.

**So the MCP server does not have to bypass RLS.** The capability the item
worried about is genuinely available: the server can be built so that the
database, not the function's own code, is what enforces whose data is whose.

## How to actually test that the policies hold

Added 2026-09-07, from what was found while applying the schema to the real
project on 2026-09-06. This section is the practical half of the finding above:
the policies were right, and the first two attempts to verify them could not
have told a working policy from a broken one.

**The SQL editor cannot test RLS on its own.** It connects as the `postgres`
role, which carries the `bypassrls` attribute, so a `select` run there returns
every row whatever the policies say. Reading two rows back is therefore not
evidence of a leak, and reading one is not evidence of protection — the result
is the same either way, which is the worst shape a security check can have.

**Impersonate the role inside the transaction instead.** `auth.uid()` reads the
JWT claim, so setting the claim makes the policies evaluate as they would for a
real request:

```
set local role authenticated;
set local request.jwt.claim.sub = '<the test user uuid>';
select * from public.tasks;
```

For the no-identity case use `set local role anon;` with no claim set. Both are
`set local`, so they last only for the transaction.

**A `42501` is not a policy denial.** Table privileges and Row Level Security
are two separate gates and Postgres checks the privilege first, so a missing
`grant` raises `42501: permission denied` before any policy is consulted. A test
that reads that as "the policy blocked me" records a pass it never earned. This
is not hypothetical: the migrations as first written granted the `authenticated`
role nothing, and the impersonated select above is what exposed it.

**Order the fixture so the insert can succeed.** `tasks.project_id` is
`not null` and references `public.projects`, so a project row has to exist for
each test user before any task row can be inserted under them.

## Naming note

Supabase renamed its keys. `sb_publishable_` replaces the old anon key and
`sb_secret_` replaces the service_role key; the legacy names are deprecated.
Documents and prompts written earlier in this project use the old names.

## Frame assessment

- **Time range.** Not applicable in the usual sense — this is a platform
  capability question rather than a claim about a period. What it does have is a
  version horizon: the key rename is mid-transition, and the legacy key names
  are on a deprecation path ending in 2026.
- **People.** It applies to Taskflow's paid-tier users, who are the only people
  whose data reaches the cloud at all. The free tier never touches Supabase, so
  nothing here bears on the majority of users.
- **Freshness.** Amended on a cycle, and actively so — the key rename is
  evidence of that. Re-read before writing the auth or the policies rather than
  building on this file's word months from now.
- **Risk if wrong.** High, and asymmetric. Being wrong that RLS protects the
  published key means every account's tasks are readable by anyone who unpacks
  the APK. Being wrong that an Edge Function can run as the caller costs design
  work, not exposure. The first warrants re-reading at build time and a test
  that actually attempts a cross-account read; it does not warrant a red flag
  today, because no table exists yet and automatic RLS is on.
- **Alternatives.** The alternative to RLS — enforcing per-account access in
  application and function code with a privileged key — was considered and is
  what the item itself named as the thing to avoid: it moves the guarantee out
  of the database and into every call site that might forget. Not researched
  further, because the finding above removes the reason to reach for it.

Sources: [Securing Edge Functions](https://supabase.com/docs/guides/functions/auth),
[Row Level Security](https://supabase.com/docs/guides/database/postgres/row-level-security),
[Authorization via Row Level Security](https://supabase.com/features/row-level-security)
