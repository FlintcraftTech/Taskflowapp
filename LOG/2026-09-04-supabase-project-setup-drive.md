# 9b577c4 — The Taskflow Supabase project exists, unblocking the whole paid tier

Walk-through drive record, opened live and appended to as it went.

Slug: supabase-project-setup
Session: 91c12720-d901-4c60-846f-40c2ba990c64, 2026-09-04
Opened live as the drive started, appended to as each step happened.

Carries a red flag, state cleared: the repository is public, so the walkthrough sends the database
password to the user's password manager and the connection values to `local.properties`, which
.gitignore excludes on two separate lines. Nothing this step produces lands in a tracked file. The
flag was cleared by that design rather than by accepting the risk, and the drive holds to it.

Capability check before handing over, per step: it begins with creating an account under the user's
own identity and ends with credentials only they can read out of their own dashboard. Claude is
barred from creating accounts in any case, and no tool available here signs up for a service on
someone's behalf. Steps 1 to 5 are genuinely theirs. Step 6 edits a file in the project, which
Claude can do on their word once they have the values.

## Actions

- 2026-09-04 16:32 — record opened. Observable checked first rather than assumed: local.properties
  currently holds sdk.dir and the buildDir line added earlier this session, and no supabaseUrl or
  supabaseAnonKey, so the item is genuinely unstarted. Step 1 given.
- 2026-09-04 16:35 — step 1 done, the user's word: signed in to Supabase with GitHub. The
  walkthrough did not name a sign-in method; GitHub OAuth is one Supabase offers, and it ties the
  Supabase account to the GitHub identity this repository is already published under. Step 2 given.
- 2026-09-04 16:38 — a gap in the walkthrough, found by driving it. Step 1's look-for was "a
  dashboard page listing your organizations", which assumed one already existed; Supabase asks a
  first-time account to name an organization before it will create a project. Settled with the
  user: **FlintCraft**, on Claude's recommendation and their agreement, to match flintcraft.tech
  and the business the Play Console account will be registered under, rather than leaving the
  user's accounts under three different names. Stated plainly at the time that whether Supabase
  allows renaming an organization later was not verified. The organization name is an
  account-level container and is not shown to Taskflow's users.
- 2026-09-04 16:44 — a second gap in the walkthrough, and this one is a security decision it never
  mentioned. The create-project form carries a Security block: Enable Data API, Automatically
  expose new tables, and Enable automatic RLS. The risk stated plainly to the user at the time: the
  anon key ships inside the Android app on every phone, so it is public by construction, and Row
  Level Security is the only thing standing between one user's key and every other user's tasks.
  Settled: Data API on (the app needs it), automatic exposure of new tables OFF, automatic RLS ON —
  so a new table arrives locked and has to be deliberately opened, rather than arriving open and
  having to be deliberately closed. Supabase's own form recommends the middle one.
- 2026-09-04 16:46 — step 2 done. Project **Taskflow** created under the **FlintCraft** organization
  on the Free plan, primary database in Oceania (Sydney), status Healthy. The project URL is
  deliberately not recorded here: this repository is public, and the URL's home is local.properties,
  which .gitignore excludes. Step 3 given (save the database password).
- 2026-09-04 16:49 — step 3 done, the user's word: the generated database password is in their
  password manager. It is deliberately not observable from this project — that is the point of the
  design that cleared this item's red flag. Step 4 given (confirm the Free plan).
- 2026-09-04 16:50 — step 4 PASSED without being handed over: the screenshot the user had already
  sent carries a FREE badge beside the FlintCraft organization in the header, which is the step's
  own look-for. Asking them to go and find it again would have been asking for something already
  on screen. Free is what the item wants for now; Pro at $25/month belongs with launch, because the
  free plan pauses a project after a week of inactivity ([0018-cloud-sync-paid-tier]). Step 5 given
  (open the Connect panel for the project URL and publishable key).
- 2026-09-04 16:52 — DEFERRED by the user, their own word: they will pick the rest up over remote
  control when they get to work. Steps 1 to 4 are done; steps 5, 6 and 7 are outstanding.

- 2026-09-04 19:00 — the deferral was short-lived: the user resumed within the same session from a
  mobile browser over remote control. Two things about the walkthrough failed on that surface and
  are worth recording. The link it supplies uses Supabase's `_` project placeholder, which resolves
  only when a project is already open — from a fresh tab it lands on a project picker instead, so
  the step had to become "tap the Taskflow card, then the Connect button". And the values are not
  on the panel's first screen: they sit inside the Kotlin snippet under the install step, which
  needs scrolling to.
- 2026-09-04 19:02 — a correction given to the user, because it changed what they did. Earlier in
  this drive they were told not to paste the key into the chat. That was over-cautious: the
  publishable key ships inside the Android app on every phone and is public by construction, which
  is precisely why automatic RLS was turned on at creation. The database password is the secret,
  and it never travelled. Reading the two values out over chat was therefore the right route on a
  phone, where editing a file is not possible.
- 2026-09-04 19:03 — step 6 done by Claude on the user's word, since they had no way to edit a file
  from a mobile browser. `supabaseUrl` and `supabaseAnonKey` written into `local.properties` with a
  comment recording why they are safe there and that the database password must never join them.
  Verified rather than assumed: `git check-ignore` reports the file excluded by .gitignore line 15,
  and `git ls-files` confirms it is untracked, so neither value can reach the public repository.

## Outcome

done — steps 1 to 6 walked to their end and the observable checked: `local.properties` now carries
a `supabaseUrl` line and a `supabaseAnonKey` line, both with values, which is exactly what the item
named as its evidence.

Step 7 — telling a planning session the project exists — is the one part left, and it is not work
so much as a handover: it is what releases [0018-cloud-sync-paid-tier] and, through it,
[0020-remote-mcp-server], [0019-ai-choice-flow-and-mcp-setup] and
[0021-strategy-doc-reconciliation-paid-tier]. This record is that notice.

### Superseded deferral

An earlier entry in this file recorded the item as deferred at 16:52. That was true when written and
is left in place as the honest sequence; the outcome above supersedes it.

Resume note, now spent, kept for the sequence: **step 5**: open the project's Connect panel
(`https://supabase.com/dashboard/project/_?showConnect=true&framework=androidkotlin&connectTab=mobiles`),
read the project URL and the publishable (anon) key, then step 6 adds them to `local.properties` as
`supabaseUrl=` and `supabaseAnonKey=`, and step 7 tells a planning session the project exists, which
is what releases the four items held against this one.

What is already true, so a later session does not redo it: the Supabase account exists (signed in
with GitHub), the organization is **FlintCraft**, the project is **Taskflow** on the **Free** plan
with its primary database in Oceania (Sydney), the generated database password is in the user's
password manager, and the project was created with the Data API on, automatic exposure of new
tables off, and automatic RLS on.

One practical note for the resume: step 6 edits `local.properties` in this project folder on this
machine, and that file is deliberately machine-specific and git-ignored. Driving this machine
remotely reaches it; working on a different machine would not.
