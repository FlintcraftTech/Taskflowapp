# [HASH] — /plan [rls-test-steps-bypass-rls]: deleted as a finding, not work — the entry it existed to fix had already left the queue, so the knowledge went to the research file

The capture's job was to stop [supabase-apply-cloud-migrations] carrying security
test steps that could not work: they query the tables in Supabase's SQL editor,
which connects as `postgres` and is exempt from Row Level Security, so they return
every row whether the policies hold or not. A test that gives the same answer on a
pass and a fail is worse than none, because it leaves a record saying the security
was checked.

But the entry it existed to fix had been driven on 2026-09-06 and had left the
queue. Its steps were rewritten live during that drive and the corrected ones
actually ran. So the item's stated job had nothing left to act on, and asking what
files it would change returned nothing — which makes it a finding rather than work.

Routed to `workshop/resources/research/supabase-rls-and-edge-function-identity.md`,
which already covered what the policies should say but not how to test that they
hold. The new section carries the impersonation SQL verbatim, the fact that a
missing grant raises `42501` before any policy is consulted so that error is not a
policy denial, and that a project row must exist before a task row can be inserted.
Its index line was extended in the same move.

**Queue changes:** deleted after the fold; [cloud-schema-missing-grants]'s
reference to it was repointed at the research file.

**Work processed:** deleted — [rls-test-steps-bypass-rls].
