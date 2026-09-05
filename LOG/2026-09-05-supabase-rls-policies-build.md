# 0bd8c64 — Cloud schema and its Row Level Security policies written, before any sync code exists

Taskflow's four cloud tables now exist as SQL, with the policies that make each row reachable only by the
account it belongs to. This is the security model for everything that will ever reach the cloud, written
first rather than discovered part-way through a sync build.

**Why the policies carry the whole weight, and why that is unusual here.** The publishable key ships
inside the Android app on every phone, so it is public by construction and cannot be treated as a secret.
Row Level Security is the only thing standing between one account's key and every other account's tasks.
That also forces the shape of the work: RLS with no policies denies the app too, so a policy has to ship
in the same move as the table it protects — which is why the item was widened in planning from
policies-only to owning the schema as well. As captured it described policies for tables nothing had
created, and so could not say which files it changed.

Refused in planning and not revisited here: folding this into [0018-cloud-sync-paid-tier] as a constraint
that each table ships with a policy. That leaves the security model to be decided part-way through a
large sync build, which is the item's own argument inverted. Also refused: a service-role key letting the
MCP server bypass RLS and enforce per-account access in its own code — it moves the guarantee out of the
database and into every call site that might forget, and the research showed it unnecessary, since a
Supabase Edge Function can run scoped to its caller.

**The failure mode the SQL is written against, because it is silent.** For a caller with no session the
identity function returns null, and a comparison against null evaluates to null rather than false —
phrased as a negation or an `or`, that widens to every row instead of none. Every policy here is a plain
conjunction of an explicit not-null check and an equality, with no negation and no `or` anywhere.

One schema decision was settled rather than passed on: cloud rows key on a UUID, because the local Room
tables' autoincrementing integer cannot be the cloud key — two devices on one account both generate id 1.
Mapping local ids to these UUIDs is the sync build's work, named here so it does not invent a key shape
around code it has already written.

The research this rests on was re-read before writing the SQL, as the item instructed, since Supabase
amends it on a cycle and the key names are mid-rename.

**Files touched:** `supabase/migrations/0001_initial_schema.sql` (created), `supabase/migrations/0002_rls_policies.sql`
(created), `supabase/README.md` (created).

**Routed to Captures:** none.

**Tick:** done, UNCONFIRMED — the SQL has not been run against a database. Applying it and proving a
cross-account read is denied is [supabase-apply-cloud-migrations], which is where a database exists.
