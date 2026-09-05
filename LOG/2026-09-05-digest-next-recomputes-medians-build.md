# [HASH] — Method defect reported: the queue digest's --next recomputes medians the ladder requires fixed

A report to the No code method project, sent after Alex approved the exact text.

**What was seen.** plan.md fixes each section's median line count and median age at the opening and holds
them for the pass, and states the reason: recomputing mid-session lets an entry swell past the median and
re-enter a group already worked through, so the group stops shrinking. `queue_digest.py --next`
recomputes both from the section as it currently stands. During a planning session on 2026-09-05, long
entries left Unprocessed as they were processed, the medians fell, and `--next` three times offered an
item the opening's medians had placed in a lower rung. Passing `--picked` did not change it: the
disagreement is about which entries are in the long-and-old group, not about the alternating rung's
parity.

**Why it was worth reporting rather than working around.** The override happened only because that
session was holding the opening's medians and noticed the mismatch. A session that takes the tool's
answer gets a different processing order with nothing indicating it diverged from the documented ladder —
the failure is silent, and the tool is the thing the doc tells sessions to ask.

The report deliberately does not propose the fix. The script cannot know the opening's medians unless it
is told them, so the choices are roughly that `--next` takes them as arguments, that the digest reports
the medians it used so a divergence is visible, or that plan.md stops promising they are fixed. Which is
right depends on the plugin's own design and is that project's call. Both quoted passages were read from
the installed plugin's own files before drafting, so the report quotes rather than paraphrases, and it
names the installed version.

Refused, and corrected in the same exchange it was proposed: flavouring this `[user]`. Claude writes the
file and can do every part of the work — needing Alex's approval on outbound text is the standing
approval rule, not a reason the work is hers.

**Files touched:** `../../No code method/INBOX/2026-09-05-from-taskflow-digest-next-recomputes-medians.md`
(created), `INBOX/sent.md` (one line, newest-first).

**Routed to Captures:** none.

**Tick:** done, confirmed — both observations read back: the report exists in the receiving project's
INBOX, and `sent.md` carries the line naming it.
