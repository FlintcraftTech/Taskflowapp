# [HASH] — Chat-level record for the twelve-item run: corrections given, decisions reached in conversation, and work belonging to no item

Date: 2026-09-05 11:20

This session ran across 2026-09-04 and 2026-09-05.

The close wrote seventeen entries, so what belongs to the chat rather than to any one work item
lives here instead of inline.

## Also in this chat

**Two corrections Claude gave the user, both of which changed what they did.** During the
`[bug-report-email-address]` walk-through they were told not to paste the Supabase publishable key
or the Workspace values into the chat. That was over-cautious and was corrected in the moment: the
publishable key ships inside the Android app on every phone and is public by construction, which is
exactly why automatic Row Level Security was turned on at project creation. The database password is
the secret, and it never travelled. The correction mattered practically — the user was on a mobile
browser and could not edit a file, so reading the values out over chat was the only route.

The second correction was larger. Both parties spent an hour reasoning about flintcraft.tech's email
on the assumption that its DNS was at GoDaddy, including a whole discarded branch about whether
moving nameservers to Cloudflare was too big a change. A live lookup settled it: the registrar is
GoDaddy but the **DNS is served by Netlify**, and every record goes into Netlify's panel. Google's
own "Go to domain host" button sent the user to GoDaddy, which would have had them adding records
that do nothing. The same lookup showed the domain had no MX records at all, which explained the
GoDaddy address the user could not find — no mail had ever been configured for it.

**A user proposal that beat Claude's, recorded because the reasoning is theirs.** Claude proposed
`bugs@flintcraft.tech` as a forwarding alias. The user asked why not use the domain's existing
address instead, and that was the better call — it cost nothing extra, was not personal, and already
existed. It was overtaken only when the plan behind that address turned out to be lapsed or confused
with another of their domains. Claude's proposal was solving for a tidy name, which is not worth
paying for.

**A decision reached in conversation that belongs to no queue item.** The user asked why they should
not simply print their personal email address on the Report-a-bug screen. Three reasons were given
and they accepted: it ships inside the app and sits in a public repository, so it gets scraped
permanently; it cannot be changed for anyone already on a released version, where an alias is
repointed in seconds; and it repeats by another door exactly what the organization Play account was
chosen to avoid — their own identity going out to every stranger who uses the app.

**Work done outside the queue, on the user's instruction.** An outbound message was drafted, shown
in full, and sent on their explicit approval to the flintcraft.tech project's mailbox, recording that
flintcraft.tech moves to Netlify by 5 May 2027 and vibe-ide.app by 10 April 2027. The dates were
given as bare days and the year was checked with them rather than assumed. Logged in `INBOX/sent.md`.
The scope boundary was named at the time: choosing where FlintCraft's email and domain live is that
project's work, not Taskflow's.

**Research filed as part of using it**, at
`workshop/resources/research/domain-email-hosting-for-the-bug-report-address.md`, with its index
line. It carries the email-hosting options and their prices, the finding that Netlify provides no
email service at all so email is always a separate provider, and the finding that a domain transfers
at any time rather than at renewal. Its own frame assessment flags freshness as its weak point —
prices and free tiers in that corner change continuously.

**Two environment facts written into TOOLS.md in the moment**, beyond the correction
`[tools-md-device-capability]` shipped: the APK under `intermediates/` carries the test-only flag so
`adb install` needs `-t`, and a launch on the phone is not evidence that new code is on it, because
Android Studio built without deploying and only the install timestamp showed it.

**A build-discipline note.** Every one of the twelve built items carried its depth field and its tick
form, and the build working file's account matched what the session remembered. No item carried a
rule-gate disposition, so none was transcribed.

**The credential scan.** It flagged three FlintCraft email addresses across these entries. All three
are addresses intended to be public — one of them is what the Report-a-bug screen will print. Stated
plainly: that check matches credential shapes only. It cannot tell whether ordinary prose names a
real person or a real situation, so it is not a verdict that these documents are safe to publish.

## Walk-through outcomes

- `[install-current-build-on-device]` — **done**. Walked to its end and the observable checked over
  adb rather than assumed. Removed from Processed.
- `[supabase-project-setup]` — **done**. Deferred at 16:52 and resumed the same session at 19:03
  from a mobile browser. Observable checked: `local.properties` carries both values, verified
  git-ignored and untracked. Removed from Processed. It carried a red flag, cleared at the planning
  session that scoped it by the design that keeps the database password out of the project folder;
  the drive held to that design throughout.
- `[first-end-to-end-test]` — **handed across and in progress**, which is none of the three standard
  outcomes. The user took it on and is running it through their working day. It closes when they
  bring the notes to a planning session, so it is neither done nor deferred. Left in place.
- `[bug-report-email-address]` — **halted on an external wait**, with the substantive work done.
  Workspace bought, domain verified, MX record live and checked. The admin account is not
  provisioned yet, so the `hello@` alias cannot be added and no address exists for the screen to
  print. Left in place. Its QUEUE.md walkthrough is substantially superseded and its record says how.

Routed to Captures: `[strategy-page-double-header]` filed mid-run; `[supabase-rls-policies]`,
`[stale-app-build-folder]`, `[run-instrumentation-tests]` and
`[play-account-payments-profile-known]` filed by the /rescan run at the end of this session; and
`[log-index-month-rollover-blocked]` filed by the close itself.

**One close step did not run, and was skipped deliberately.** August has ended, so the index's
month rollover was due — but 38 of `LOG/index.md`'s roughly 100 lines point at entries whose
filenames carry no date, so "that month's lines" cannot be identified without opening around 38
entry files. Extracting only the 21 identifiably-August lines would have left the index reading
September, then a gap, then undated older entries, which is a worse file than the one that exists.
Filed as `[log-index-month-rollover-blocked]` with the two routes out.

Advisory: filed — forward-advisory
