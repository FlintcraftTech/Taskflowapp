# Google Play developer account — organization vs personal

Read 2026-09-03, from Google's own Play Console help plus corroborating
practitioner sources, to settle Taskflow's Play account type and to write
[play-console-subscription-product]'s walkthrough against something checked
rather than remembered.

## What it settles

**Both account types can sell subscriptions.** A registered business is not
required to take money on Google Play. The account type is therefore a choice
about identity and paperwork, not about capability, and it cannot be switched
afterwards by editing a setting.

**What each type asks for.**

- **Personal** — developer name, legal name and address (carried on the linked
  Google Payments profile), contact email and phone, developer email. A
  government identity document for verification.
- **Organization** — everything above, plus a **D-U-N-S number**, the
  organization's name and address, an organization **phone number**, an
  organization **website**, and a contact name, email and phone. Business
  registration or incorporation documents from a government authority, proof of
  the organization's physical address (a registered-agent address is not
  accepted), and verification of an authorised representative who must appear on
  the business registration and supply their own personal ID.

**What is shown publicly, which is what decides it for a solo developer.** The
payments profile's legal name and address appear on customer receipts, and an
organization account additionally displays the developer phone number publicly.
On a personal account those details are the individual's own name and home
address, going to every subscriber.

**The D-U-N-S number takes up to 30 days.** It is free to request — Dun &
Bradstreet operates a free route specifically for Google developer accounts —
though a paid expedited option (around 8 days) is offered during the flow.
Timing varies by region and can be instant. Google offers an alternative
verification path only to organizations genuinely unable to obtain a number.

**Costs are identical on both types**: a one-off $25 registration fee, 2-Step
Verification on the Google account, and a payments profile carrying tax
information and a bank account for payouts. Google's revenue share — 30% of
purchases, 15% of subscriptions after the first year — is the same either way,
so revenue does not bear on the choice.

## Frame assessment

- **TIME RANGE** — applies to an account opened now. The requirements describe
  present-day registration; nothing here is a forecast, and the 30-day D-U-N-S
  figure is a stated maximum rather than a trend.
- **PEOPLE** — applies to the person registering, who here is a solo developer
  intending to trade through a registered business. The receipts and public
  phone-number findings are the ones that land differently for a solo developer
  than for a company with an office, which is exactly why they were sought.
- **FRESHNESS** — amended on a cycle. Google has revised developer verification
  requirements repeatedly, most visibly in the 2023 trust-and-transparency
  policy round and again through the 2026 verification changes. Re-read before
  driving the registration.
- **RISK IF WRONG** — moderate and recoverable in one direction, not in the
  other. Being wrong about a document or a fee costs a delay. Being wrong about
  the account type is not recoverable by editing a setting, and being wrong
  about what appears on receipts would publish a home address. That asymmetry is
  what warranted checking rather than assuming.
- **ALTERNATIVES** — the two account types were the whole option set and both
  were researched. No third route exists: Google offers alternative verification
  only where a D-U-N-S number cannot be obtained, which is a fallback within the
  organization type rather than a separate type.

## Sources

- [Required information to create a Play Console developer account](https://support.google.com/googleplay/android-developer/answer/13628312?hl=en)
- [Choose a developer account type](https://support.google.com/googleplay/android-developer/answer/13634885?hl=en)
- [Dun & Bradstreet — D-U-N-S for Google developers](https://www.dnb.com/en-us/smb/duns/google-developers.html)
