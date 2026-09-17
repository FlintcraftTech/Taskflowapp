# [HASH] — /plan [supabase-apply-0003-grants]: walkthrough rewritten to carry the SQL rather than point at a research file

**Kind: processed.** Recorded 2026-09-17 at 16:01, read from the clock, during a planning session.

The item runs `0003_grants.sql` against the live Supabase project — proving the repository and the database
agree, since the grants were applied by hand on 2026-09-06 — and then checks a signed-in user can read
their own rows, which is what the whole paid tier rests on.

The capability check confirmed it must stay `[user]`, and found no route round it: no Supabase CLI on this
machine, no `psql`, nothing linking the project folder to the live database, and no Supabase access
recorded in TOOLS.md. Every route would need Alex's credentials, which Claude does not handle. Genuine
incapability rather than a rule, so no say-so would move it.

The change is to the walkthrough. As filed, its step 4 told her to impersonate a signed-in user and pointed
at `supabase-rls-and-edge-function-identity.md` for how — three jobs in one step, for a no-code developer,
ending in SQL she would have to assemble with a user identifier she would have to find. The steps now carry
the SQL as blocks to paste, with a step that fetches the identifier first, and a stated outcome for the case
where the project has no accounts at all.

Two facts from that research file were lifted into the item rather than left to be fetched, because reading
them wrong is how a check records a pass it never earned: the SQL editor connects as a role that bypasses
row protection entirely, so a plain query there proves nothing; and `42501: permission denied` is a
**privilege** failure, not a policy denial — Postgres checks the grant before it consults any policy. That
second one is not hypothetical, it is how the missing grants were found on 2026-09-06.

The verified form was used rather than a tidier one nobody has run.

**Queue changes:** [supabase-apply-0003-grants] rewritten with a paste-ready walkthrough and cleared to run.

**Work processed:** kept — [supabase-apply-0003-grants].
