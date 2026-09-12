# Where the bug-report address can live, and what each route costs

Read 2026-09-04, while driving `[bug-report-email-address]`. The item needs one thing — an address
Taskflow's Report-a-bug screen can print — but answering it turned on facts about email hosting and
domain transfers that would otherwise be researched again.

## What the options actually are

**Netlify registers new domains but cannot RECEIVE a transfer of an existing one.** Corrected on
2026-09-12 from a message sent by the project that owns the domain, which read Netlify's own
documentation on 2026-09-09: transfers run outward only, and what Netlify offers for a domain
registered elsewhere is DNS delegation — the registration stays put and the nameservers point at
Netlify. Registering a new domain there is a different operation, which is where the confusion came
from. Nothing else in this file falls: the transfer mechanics below, and the separation of registrar
from mail provider, are unaffected.

**Email is always a separate provider from the registrar.** Netlify, where the flintcraft.tech site
is hosted, registers domains but provides no email service at all — no mailboxes and no forwarding.
So there is no bundle to buy, and the registrar decision and the email decision are independent.
That is the finding that unblocked the item: MX records can point at a mail provider while the
domain still sits at its current registrar, so email never waits on a transfer.

**Forwarding versus a real mailbox is the dividing line, and "send from" is what decides it.** A
forward is a rule, not an inbox — no login, no storage, nothing extra to read. It only receives.
Sending as the address needs a real mailbox or an SMTP add-on.

| Route | Receives | Sends | Cost |
|---|---|---|---|
| Cloudflare Email Routing | yes | no | free, but requires the domain's **nameservers** to move to Cloudflare, which hands it the whole domain's DNS including the website |
| ImprovMX free | yes | no | free — 1 domain, 25 aliases, 500 forwards/day. Works by adding MX records; the domain does not move |
| ImprovMX Premium | yes | yes (6,000 SMTP sends/month) | $9/month, or $7.65/month billed annually (~$92/year) |
| Zoho / Migadu / Purelymail | yes | yes | historically £10–20/year — noted, not verified this session |
| Google Workspace Business Starter | yes | yes | $7/user/month annual ($84/year), or $8.40 month-to-month ($100.80/year) |
| GoDaddy's cheapest plan | yes | yes | renews at $167.40/year |

**Google Workspace was the direction of travel**, at half GoDaddy's price and roughly seven times a
bare mailbox. What it buys is Gmail rather than capability — and the deciding argument was
behavioural rather than technical: an existing address on the domain at GoDaddy has gone unread for
however long, so a mailbox the user does not already live in is a liability whatever it costs.

## Transferring a domain away

A domain can be transferred **at any time**; you do not wait for the renewal date. The blockers are
ICANN's 60-day locks — 60 days since registration, since a previous transfer, and since any change
to the registrant contact details. A transfer also *adds* a year to the expiry rather than
forfeiting paid-up time, so moving early costs nothing.

## Frame assessment

- **TIME RANGE** — not applicable in the product sense: this is about the project's own
  infrastructure rather than about Taskflow's users or the horizons the app addresses.
- **PEOPLE** — it applies to the one person who will read the bug reports, and the argument turned
  on exactly that: an inbox he does not already open is worse than a costlier one he does. It says
  nothing about the people *sending* bug reports, who only need an address that works.
- **FRESHNESS** — poor, and this is the weak point. Free tiers and per-seat prices in this corner
  are amended continuously, and ImprovMX's and Zoho's free tiers have both been cut back before.
  Re-read every price here before acting on it; none should be quoted as current more than a few
  months out.
- **RISK IF WRONG** — low and self-correcting. A wrong price is discovered at the checkout, and a
  wrong provider is a change of MX records rather than anything irreversible. The one genuinely
  irreversible-feeling move — transferring the domain — was established as safe to do early, so
  being wrong about timing costs nothing either. No red flag warranted.
- **ALTERNATIVES** — several were considered and ruled out with reasons: using the user's personal
  Gmail directly (rejected — it ships inside the app on every phone and into a public repository,
  cannot be changed without releasing an update, and repeats the exposure the organization Play
  account was chosen to avoid); using the existing unread GoDaddy address (a good suggestion of the
  user's, overtaken when the plan turned out to be lapsed or misremembered); Cloudflare Email
  Routing (ruled out on the nameserver move, and on not being able to send); a web form on
  flintcraft.tech and a GitHub issue (both ruled out earlier, on 2026-08-31, on audience fit). Not
  investigated: self-hosting mail, which nobody wanted.
