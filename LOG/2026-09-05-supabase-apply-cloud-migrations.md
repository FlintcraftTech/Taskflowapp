# [HASH] — /plan [supabase-apply-cloud-migrations]: the user step split out of the RLS work, carrying the cross-account read test that actually proves the policies

Session date and time: 2026-09-05, afternoon planning run.

Reasoning for the widened scope lives in `2026-09-05-supabase-rls-policies.md`; this records the split.

Writing the SQL is Claude's work. Applying it is not: the capability check found no Supabase CLI on this
machine, and both available routes need a credential Claude is barred from handling — the CLI wants a
project access token and the dashboard wants the user's Google sign-in. So the applying and the proving
became a `[user]` item of their own, held against the build, rather than a clause buried in it.

What the item is really for is the test rather than the applying. Its walkthrough ends with two checks a
person has to see: signed in as one test user, querying the tasks table must return that user's row and
not the other's; and querying with no session at all must return nothing. The second is the null-identity
case the research named as the silent failure — a loosely phrased policy passes every other check and
fails only that one. Until both run, the policies are text in this repository rather than rules on a
database.

Its observable is stated as absent on purpose: the tables and policies live inside the user's Supabase
project, which nothing here can see, so a later session asks rather than checks.

**Queue changes:** [supabase-apply-cloud-migrations] created and placed in Processed below the readiness
line, `Blocked by: [supabase-rls-policies]`.

**Work processed:** kept — [supabase-apply-cloud-migrations].
