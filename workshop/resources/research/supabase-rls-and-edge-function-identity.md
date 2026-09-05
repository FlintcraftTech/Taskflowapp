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
