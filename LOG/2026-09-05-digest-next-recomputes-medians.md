# [HASH] — /plan [digest-next-recomputes-medians]: the digest's next-pick disagreed with the ladder three times, filed as a report to the method project

Session date and time: 2026-09-05, afternoon planning run, at /rescan.

Surfaced by /rescan. plan.md fixes each section's median line count and median age at the opening and
holds them for the whole pass, with a stated reason: recomputing mid-session lets an entry swell past the
median and re-enter a group already worked through, so the group stops shrinking. `queue_digest.py
--next` recomputes both from the section as it currently stands. As long entries left Unprocessed during
this session the medians fell, and `--next` three times offered an item the opening's medians had placed
in a lower rung. `--picked` did not change it, because the disagreement is about which entries are in the
long-and-old group rather than about the alternating rung's parity.

Worth reporting rather than working around, for one reason: the override happened only because this
session was holding the opening's medians and noticed the mismatch. A session that takes the tool's
answer gets a different processing order from the documented ladder with nothing indicating it diverged.
The failure is silent, and the tool is what the doc tells sessions to ask.

The report is to say what was seen and not to propose the fix as obvious. The script cannot know the
opening's medians unless told, so the options are roughly that `--next` takes them as arguments, that the
digest reports the medians it used so a divergence is visible, or that plan.md stops promising they are
fixed — a design call belonging to the No code method project.

Refused: flavoring it `[user]`. Claude recommended that and corrected it in the same exchange — Claude
writes the file and can do every part of the work, and needing the user's approval on outbound text is
the standing approval rule rather than a reason the work is theirs.

**Queue changes:** [digest-next-recomputes-medians] created and moved to Processed, cleared to run.

**Work processed:** kept — [digest-next-recomputes-medians].
