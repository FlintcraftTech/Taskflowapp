# [user] Create the bug-report email address — 2026-09-05 (walk-through)

The live drive of [bug-report-email-address] during a /next run. **Outcome: done**, walked to its end.

## The address

`bugs@flintcraft.tech`, a Google Workspace **alias** on the flintcraft.tech domain. Both decisions the
item left to Alex are settled by that: the domain is flintcraft.tech, and the shape is an alias rather
than a separate mailbox — free, no extra licence, and mail lands in the inbox she already reads.

## Steps as driven

1. Sign in to whatever manages the domain's email. This took most of the drive. She had set Workspace up
   the night before and could not remember the admin address or its password. Rather than guess, the
   domain's own records were read: `flintcraft.tech` MX points at `smtp.google.com` and carries Google's
   verification TXT, neither of which exists without a completed Workspace setup — so an admin account
   had to exist. Trying the likeliest admin address at the sign-in page asked for a password rather than
   rejecting it, which confirmed the account. She recovered it herself; password recovery is hers alone
   and was not touched here.
2. Gmail's own setup card claimed "Turn on Gmail — Required", which would have made an alias silently
   undeliverable. Checked before creating anything: already activated, the card was stale. Google's
   activation page did warn that routing can take up to 24 hours to settle, which is why a slow test
   would not have meant a mistake.
3. Alias created in the Admin console under the user's **Add alternative emails**.
4. Tested. The message was sent from her personal mail account — one the alias does not forward from,
   which is what makes arrival evidence of real delivery rather than a loop — and arrived in the Workspace
   inbox, labelled External. Sent by Claude through the Gmail connector, on Alex's explicit approval of
   the exact text.

## Carried forward

**The address goes to the Report-a-bug screen wording**, which is step 4 of the item's walkthrough and
belongs to [help-thanks-report-content]. Filed as the capture [bug-report-address-is-bugs-at-flintcraft]
rather than left in conversation, since a build may not edit queue items.

**An alias receives but does not send.** Replying to a bug report *as* `bugs@flintcraft.tech` would need
a send-as configured in Gmail, which nobody has done. Not needed for the screen to print an address, so
it is recorded rather than filed.

## Two corrections worth keeping

Claude handed over two wrong paths for the Claude Code CLI before verifying one by running it, during
the plugin detour that ran alongside this item. And `gh auth status`'s "Logged in to … FlintCraftTech"
was read as her identity, producing a wrong warning that a GitHub post would go out under the wrong
account; `FlintcraftTech` is an organisation and `gh api user` returns `its-coughfee`. Both are the same
failure — asserting what a surface does instead of reading it.

## Note on the queue-lint secret scan

Filing the capture made the hook flag `bugs@flintcraft.tech` in QUEUE.md as an email address in a file
that gets committed. That is expected and accepted: the address exists precisely to be printed inside a
shipped app, so publishing it in a public repository costs nothing. Recorded because the flag will recur
on every commit touching that entry.
