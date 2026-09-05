# [HASH] — Deleted after the SPEC edit: §Settings no longer lists the AI tier, so the two sections that disagreed now agree with the build

Recorded 2026-09-06, 00:29.

SPEC contradicted itself in one word. §Settings listed the AI tier among the controls Settings holds;
§Side menu put a "Turn on AI" entry in the drawer's pinned bottom section, and §Tier model said the same.
The device follows the drawer. Nothing was broken, but a later session reading §Settings would reasonably
have concluded the entry was missing and built it, putting one control in two places.

This is a SPEC edit rather than a build, and product truth is written at planning time with the user
present — so it was made here rather than queued. A SPEC sentence describing a mechanism the project does
not have is a defect corrected when noticed.

The drawer won on evidence rather than preference: it is where the build put it, and two SPEC sections
already said so against one. §Settings now lists three controls and states outright that the AI tier is
reached from the side menu's own row instead, so the correction is positive rather than a silent
deletion — a later reader is told where the control is, not merely left without a mention.

**What stays open, and was said to Alex rather than closed by this edit:** matching SPEC to the build
settles the contradiction, not whether the drawer is the better home. There is a reasonable case that a
tier control is something people go to Settings looking for. That would be a design change with real work
behind it, and it becomes its own item whenever she wants it.

**Queue changes:** `SPEC.md` §Settings rewritten; capture deleted.
**Work processed:** deleted — [spec-ai-tier-location], content relocated into SPEC.
