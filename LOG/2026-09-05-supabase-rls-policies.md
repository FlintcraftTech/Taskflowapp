# [HASH] — /plan [supabase-rls-policies]: widened from policies-only to owning the cloud schema, after a lookup settled that the MCP server can read through RLS rather than bypassing it

Session date and time: 2026-09-05, 12:0x (processed during the afternoon planning run).

The capture described Row Level Security policies for tables that nothing had created, so it could not
say which files it changed — there is no cloud schema in this repository at all. That fails the
buildability check's second limb, and the honest ways out were two: fold it into
[0018-cloud-sync-paid-tier] as a constraint that a policy ships with each table, or widen it to own the
schema as well as the policies. Claude recommended the second and the user agreed. Refused: the fold,
because it would leave the security model to be decided part-way through a large sync build, which is
the item's own argument inverted.

Three questions the capture left open. Two were already answered elsewhere in the queue:
[0020-remote-mcp-server] makes Supabase Auth the identity provider, so a Taskflow account is a Supabase
Auth user, and a row is tied to that identity by a `user_id` column. The third — whether the MCP server
reaches rows through the same policies or through a service role that bypasses them — was a genuine
security decision, and Claude offered to look it up rather than answer from recollection.

The lookup settled it: a Supabase Edge Function can run in a mode where the platform validates the
caller's token and hands the function a database client already scoped to that user, so the guarantee
stays in the database rather than moving into the server's own code. It also produced the reason the
policies matter more here than in an ordinary web app — the publishable key is safe to expose *only*
because RLS checks the caller's token, and the key ships inside the Android app on every phone — and a
silent failure mode: a no-session identity is null, and a policy phrased as a negation or an `or` can
widen to every row instead of none. Filed as
`workshop/resources/research/supabase-rls-and-edge-function-identity.md` with its index line.

One schema decision was settled here rather than passed on: cloud rows carry a UUID primary key, because
two devices on one account both generate local id 1. Naming it now stops the sync build inventing a key
shape around code it has already written.

No SPEC edit was owed. SPEC §Claude integration via remote MCP already says every tool call is answered
against that account's cloud data and no other's, and that a request the server cannot tie to an
authenticated account is refused. Row Level Security is the mechanism serving that sentence, which
SPEC's own admission rule keeps out.

**Queue changes:** [supabase-rls-policies] rewritten and moved to Processed, cleared to run;
[supabase-apply-cloud-migrations] split out and held against it (its own entry);
[0018-cloud-sync-paid-tier] re-held against both, its previous `Blocked by:` naming
[supabase-project-setup], which shipped on 2026-09-04 and no longer resolved.

**Work processed:** kept — [supabase-rls-policies].

Advisory: filed — see the chat-level record.
