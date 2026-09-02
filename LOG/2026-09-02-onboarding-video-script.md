# [HASH] — build [onboarding-video-script]: the six-page video written, with the three pages needing a live Claude conversation marked

Split out of [onboarding-video-content] in planning on 2026-08-25, which had bundled a design decision with a production job: what the video *says* is designable now, while filming it waits on the Claude integration existing. This is the design half.

The video carries the whole free-versus-paid choice in onboarding, so what it demonstrates is a product decision rather than a production detail — and it was writable entirely from what is already settled: SPEC's tier model, §Claude integration via remote MCP, §Strategy doc, and `SYSTEM-PROMPT.md`'s account of how Claude explores life areas, suggests projects and reconciles the Strategy doc.

**The through-line the script commits to:** one claim made concrete four times — Taskflow is reachable from wherever the user already talks to Claude. There is no chat UI inside Taskflow, so the value is that the app shows up in a conversation the user was going to have anyway. The script opens on the *other* app rather than on Taskflow's own screens, because leading with Taskflow would set up the expectation of an in-app assistant, which is exactly what this is not.

It closes on the free tier working alone. The choice screen follows immediately, and it should follow an honest statement of what declining actually costs — ending on the paid tier's best moment would make "Skip AI for now" feel like a mistake, which is not what the tier model says it is.

Each page carries what is on screen, the claim it makes, and a rough duration, totalling around 95 seconds. Three pages are marked as needing a **live Claude conversation** and three as plain screen capture, because that is what decides how much of the filming can be done today: the live ones cannot be shot until the MCP server exists.

Two filming notes were written in from product truth rather than left to the filming session: a task Claude adds must appear looking like any other task, with no "added by Claude" badge, since SPEC is explicit there is no second class of task; and Claude must be shown *asking* during reconciliation, never silently editing, which `SYSTEM-PROMPT.md` forbids — a video showing it would promise a product that is not being built.

**Files touched:** ONBOARDING-VIDEO-SCRIPT.md (new). No app code.

**Routed to Captures:** none.

**Tick:** done, confirmed: ONBOARDING-VIDEO-SCRIPT.md covers all six pages, each with on-screen content, its claim and a rough duration, and marks which three need a live Claude conversation. Read back after writing; no app code changed.
