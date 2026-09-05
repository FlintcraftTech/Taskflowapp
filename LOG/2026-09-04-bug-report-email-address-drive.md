# 9b577c4 — Google Workspace bought and flintcraft.tech's mail routed, one step short of the address

Walk-through drive record, opened live and appended to as it went.

Slug: bug-report-email-address
Session: 91c12720-d901-4c60-846f-40c2ba990c64, 2026-09-04
Opened live as the drive started.

Taskflow's Report-a-bug screen sends people to an email address, and that address does not exist
yet. The route was settled on 2026-08-31: a dedicated email address, chosen over a web form on
flintcraft.tech and a GitHub issue, which lost on audience fit — most of Taskflow's users are not
technical and will not have a GitHub account.

Capability check before handing over: creating a mailbox or an alias needs access to whatever hosts
the domain's email, which is an account of the user's that Claude has no reach into. Re-confirmed
this session — no tool available here creates a mailbox or an alias on someone's behalf. The steps
are genuinely theirs.

## Actions

- 2026-09-04 19:09 — record opened. Observable checked first: the item's own evidence is the address
  being written into [help-thanks-report-content], and nothing in the repository has changed, so it
  is genuinely unstarted.
- 2026-09-04 19:10 — the item's two stated decisions were put to the user as one proposal:
  `bugs@flintcraft.tech` as a forwarding alias. Both halves were then reopened by the user, and both
  changed. Recorded because the reasoning matters more than the outcome here.
- The user asked why not simply use the personal address they already read. Answered: it ships
  inside the app on every phone and sits in a public repository, so it gets scraped permanently; it
  cannot be changed for anyone already on a released version, where an alias is repointed in
  seconds; and it repeats by another door exactly what the organization Play account was chosen to
  avoid — their own identity going out to every stranger who uses the app. The user accepted it.
- The user then proposed using the domain's existing GoDaddy address instead. That was the better
  call and was said so plainly: it costs nothing extra, it is not personal, and it already exists.
  Claude's `bugs@` proposal was solving for a tidy name, which is not worth paying for. Overtaken
  when the user found the plan behind that address was lapsed or was being confused with another of
  their domains.
- The requirement then changed: the user wants to SEND from the address, not only receive. That
  rules out every forwarding-only route on its own, and it is why the answer moved away from an
  alias. `hello@` was settled as the name — general enough for bug reports, enquiries and outbound
  mail, and it reads as though a person will answer; `info@` was refused as the most spam-scraped
  local part there is and one that reads as a no-reply.
- 2026-09-04 19:20 — GoDaddy's cheapest plan renews at $167.40/year, which the user did not want,
  and they did not want Microsoft 365. Four searches were run rather than answering from memory, and
  the findings are filed at
  `workshop/resources/research/domain-email-hosting-for-the-bug-report-address.md` with its index
  line. The load-bearing one: Netlify registers domains but provides no email service at all, so
  email is always a separate provider, MX records can point anywhere while the domain stays put, and
  email therefore never waits on a registrar transfer.
- Also established and filed: a domain transfers at any time rather than at its renewal date,
  blocked only by ICANN's 60-day locks, and a transfer adds a year to the expiry rather than
  forfeiting paid-up time.
- Direction of travel: **Google Workspace at $84/year** for a public address on the domain, half GoDaddy's
  price and about seven times a bare mailbox. The deciding argument was behavioural rather than
  technical, and it came out of the user's own evidence — an existing address on the domain has gone
  unread, so a mailbox they do not already live in is a liability whatever it costs. Workspace is
  Gmail, which they do read.
- 2026-09-04 19:30 — the user asked for a message to the flintcraft.tech project recording that both
  flintcraft.tech and vibe-ide.app should move to Netlify, by 5 May 2027 and 10 April 2027. Drafted,
  shown in full, and sent on their explicit approval after they confirmed the year — the dates were
  given as bare days, and with today at 2026-09-04 both next fall in 2027, which was checked rather
  than assumed. Filed at `flintcraft.tech/INBOX/2026-09-04-from-taskflow-domain-transfers-to-netlify.md`
  and recorded in this project's `INBOX/sent.md`.
- Scope boundary named to the user at that point: choosing where FlintCraft's email and domain live
  is flintcraft.tech's work, not Taskflow's. Taskflow's item needs only an address that exists and
  receives, which is why the transfer went out as a message to that project rather than growing into
  this item.
- 2026-09-04 22:11 — Google Workspace bought and set up, driven step by step. A personal address on
  the domain as the first account, deliberately: that username becomes the super-admin, and the public
  bug-report address should not also be the administrator login. The public alias is added afterwards
  as an alias, which costs nothing in Workspace and can be sent from, so the whole thing stays at
  one seat.
- A live DNS lookup corrected what both parties had been assuming for an hour, and it is the finding
  worth keeping from this drive. **flintcraft.tech's registrar is GoDaddy, but its DNS is served by
  Netlify** — nameservers `dns1-4.p04.nsone.net`, zone contact `domains+netlify.netlify.com`. So
  every record goes into Netlify's panel and GoDaddy is not consulted at all. Google's own "Go to
  domain host" button sent the user to GoDaddy, which would have had them adding records that do
  nothing. The same lookup showed the domain had **no MX records whatsoever**, which explains the
  unfindable GoDaddy address: no mail had ever been configured for the domain.
- 2026-09-04 22:20 — verification TXT record added in Netlify and confirmed live by Claude against
  Google's public resolver before the user pressed Confirm, rather than taken on trust. Then the MX
  record — `smtp.google.com`, priority 1 — added and likewise confirmed live. The domain now has
  working mail routing for the first time.
- 2026-09-04 22:35 — HALTED one step short. The alias cannot be added yet: the `alex` account is not
  visible in the admin console, and Google warned provisioning can take up to 24 hours. Nothing to
  drive; it is an external wait.

## Outcome

Halted mid-drive on an external wait, with the substantive work done. Not `done`: the item's whole
deliverable is an address the Report-a-bug screen can print, and no address is usable yet.

Where it resumes: open the Google admin console's user list, open the `alex` account, and add
`hello` under "Alternate email addresses (email alias)". Then the address gets written into
`[help-thanks-report-content]`, which is this item's actual observable — nothing in this repository
changes until that happens.

**The item's own walkthrough in QUEUE.md is now substantially superseded** and should be read
against this record rather than followed. It assumed the choice was between a mailbox and a
forwarding alias on a domain hosted at GoDaddy; what actually happened is a Workspace purchase, DNS
at Netlify, and an alias on an admin account. The two decisions it asked the user to make were both
made, but not as it framed them: the domain is flintcraft.tech, and the form is a real mailbox
because the user wants to send from the address, which no forward allows.
