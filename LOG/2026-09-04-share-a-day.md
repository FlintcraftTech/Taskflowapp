# 9e24ba7 — /plan [share-a-day]: sharing a day as PNG or Markdown, the heaviest of the four despite being the smallest feature

One of four items split out of [nav-completed-history] this session; the split's reasoning is in
`2026-09-04-nav-completed-history.md`. Held below the line because there is no day screen to put the
button on until [nav-day-card-layer] ships.

The two formats were not re-decided — they were settled on 2026-08-21 per
`workshop/resources/research/android-share-format-png-vs-pdf.md`, PNG because it renders inline in a
chat thread rather than arriving as an attachment, and Markdown carried under `text/plain` because
almost no Android app declares `text/markdown`.

What this session added is the cost, found by reading the code: `StrategyScreen` already shares text
through `Intent.ACTION_SEND`, so the Markdown half has a pattern to copy, but the PNG half needs a
composable rendered to a bitmap, a cache file and a `FileProvider` declaration that does not exist yet.
That is why the smallest of the four features is the largest of the four items.

**Queue changes:** [share-a-day] filed and moved into Processed below the line, held against
[nav-day-card-layer].

**Work processed:** kept, held below the line — [share-a-day].
