# [HASH] — /plan [rotating-roster-free-choice]: deleted on Alex's word, after the code showed its premise was wrong and her redesign then superseded the question entirely

**Kind: processed.** Recorded 2026-09-17 at 16:01, read from the clock, during a planning session.

The item proposed one position in a repeating task's rotation that offers two names instead of one, with
the user picking when the instance lands. It had been split out of [rotating-roster-recurrence] on the
reasoning that a free-choice position would have to interrupt the user, and Taskflow has no surface for
that — there are no notifications in v1.

Reading the shipped code showed that premise was wrong. `Task.roster` is free text, one label per line,
and an instance simply displays whichever line its turn falls to; nothing validates a line or requires it
to name one person. So a line reading "one name or another" already renders as written, with no code at
all. What the app would not do is record which of the two was picked.

Put that way, Alex deleted it — and then reframed the whole feature, which superseded the question rather
than answering it. That reframe is [roster-as-subtasks] and its record carries the reasoning.

Worth keeping from this: the item's stated obstacle was an assumption about the code that nobody had
checked, and checking it cost one grep. The capture's account of how something works is a claim to test.

**Queue changes:** [rotating-roster-free-choice] deleted from Unprocessed.

**Work processed:** deleted — [rotating-roster-free-choice].
