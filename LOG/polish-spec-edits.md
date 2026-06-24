# 988a9fa — SPEC §Schedule view + §Side menu: drop Tomorrow's date label and reframe the side-menu opener to the ☰ button (swipe-to-open disabled)

Two one-sentence SPEC changes were each folded into a feature build below; the method keeps SPEC edits out of feature builds, so both were pulled into this single spec-edit batch — combined rather than split per-feature to keep the ceremony to one session.

§Schedule view: the date-label rule now lists only Soon and Later as showing the DD/MM date. Tomorrow no longer shows a date label — a Tomorrow task always carries tomorrow's date, so the page name is the day signal and a label would only repeat it. Today's rule is unchanged (a label only when the date has slipped into the past).

§Side menu: the opener is reframed from a left-edge swipe to tapping the ☰ button (the menu still slides in from the left as a drawer). Swipe-to-open is recorded as intentionally disabled, because the left-edge swipe collides with the spine's horizontal-swipe navigation — the ☰ is the single, discoverable opener.

These were design decisions, not bug fixes; the prior SPEC text was correct as written. The two feature builds that implement them ([tomorrow-no-date-label], [disable-drawer-swipe-open]) depend on this batch and are now unblocked.

**Files touched:**
- SPEC.md — §Schedule view date-label sentence; §Side menu opener sentence

**Routed to Captures:** none
