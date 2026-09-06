# 5fa5c85 — Deleted after the scrub: the admin address is out of the working copy in all three places, and history keeping it is accepted rather than fixed

Recorded 2026-09-06, 00:29.

The capture reported Alex's Google Workspace admin address written into a `LOG/index.md` line in a public
repository, and set out three options: leave it, rewrite the working copy accepting that history keeps it,
or rewrite the history itself.

Two corrections came out of checking, and one changed the work. **It was not one line.** The address
appeared three times across two tracked files — the index line, and twice in the body of the 2026-09-04
bug-report entry. Editing only the index line would have left two behind and read as done. In the other
direction, `INBOX/sent.md` also carries an address but `INBOX/` is gitignored, so that occurrence was never
published and needed nothing.

**The middle option was taken, and its limit is the point rather than a footnote.** Rewriting the history
of an already-public repository is large and disruptive and buys little here: anyone who has cloned it
already has the address, and it is Alex's own on her own domain rather than a third party's. Leaving it
entirely keeps it at the top of the index, which is the first thing a reader of the repo sees. So all
three occurrences were rewritten to describe the account rather than name it, at the same level of
usefulness, and **git history keeps what is already committed — that residual is accepted knowingly, not
repaired.**

A sweep of every tracked document afterwards returns no address but `bugs@flintcraft.tech`, which is
deliberate everywhere because it exists to be printed inside the shipped app.

**Queue changes:** `LOG/index.md` line 59 and `LOG/2026-09-04-bug-report-email-address-drive.md` lines 52
and 67 rewritten; capture deleted.
**Work processed:** deleted — [admin-address-in-committed-index-line], content relocated into the edits.
