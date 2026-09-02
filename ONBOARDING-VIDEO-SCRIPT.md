# ONBOARDING-VIDEO-SCRIPT.md — the AI-value video

The multi-page video shown during first-run onboarding, between the two Schedule/Projects explainer
cards and the free-versus-paid choice (SPEC §Onboarding — first run).

## What this video has to do

The video carries the whole free-versus-paid choice. The free tier is a complete product, so the
user is not being talked into anything — they are being shown what the paid tier actually is, so
that choosing "Skip AI for now" is an informed decision rather than a shrug.

The through-line is one claim, made concrete four times: **Taskflow is reachable from wherever the
user already talks to Claude.** There is no chat window inside Taskflow. The value is that the app
shows up in a conversation the user was going to have anyway.

What the video must not do: imply the free tier is broken, show a feature that does not exist, or
run long enough that the user taps past it. Total target is **75–95 seconds**.

## Filming note, up front

Two kinds of page appear below, and they cost very different amounts to make:

- **Screen capture** — Taskflow itself, recorded from a device. Scriptable and re-recordable.
- **Live Claude conversation** — a real exchange in a Claude client with the connector attached.
  These cannot be filmed until the MCP server exists and is reachable, and each one needs a real
  account with plausible data in it.

Pages 1, 2 and 6 are screen capture. Pages 3, 4 and 5 need a live conversation.

---

## Page 1 — Where this happens (~10s)

**On screen.** A phone showing the Claude mobile app, mid-conversation about something ordinary and
non-task-shaped — planning a weekend, say. No Taskflow UI at all yet.

**The claim.** This is where it happens. You talk to Claude the way you already do.

**Why it opens here.** Leading with Taskflow's own screens would set up the expectation of an
in-app assistant, which is exactly what this is not. The first thing the viewer sees should be the
surprise: the other app.

*Screen capture.*

---

## Page 2 — The connection, in one line (~8s)

**On screen.** Taskflow's own AI-setup screen, then the Claude connector list with Taskflow in it.
One tap, no configuration shown.

**The claim.** Connect it once and Taskflow is there in every Claude client on your account.

**Why.** The viewer's live objection at this point is "so I have to set something up." Answering it
in eight seconds and moving on is cheaper than letting it sit.

*Screen capture.*

---

## Page 3 — Claude puts something into Taskflow (~20s)

**On screen.** A live conversation. The user mentions something they need to do, in passing, not as
a request — the way a thing to do actually comes up. Claude offers to put it in Taskflow, the user
agrees, and the task appears. Cut to Taskflow showing it on the right day.

**The claim.** Anything you mention can land in your task list without you switching apps.

**Why this is the first Claude page.** It is the smallest, most legible version of the whole
proposition, and it is the one a viewer can immediately imagine wanting.

**Note for filming.** The task must arrive looking exactly like any other task — no badge, no "added
by Claude" marker. That is product truth (SPEC §Project context) and the video should not
accidentally suggest otherwise.

*Live Claude conversation.*

---

## Page 4 — Claude knows the shape of your life (~22s)

**On screen.** A live conversation where Claude, asked about something, answers with the user's
areas of life in view — noticing that a new task belongs to an area, or asking a gentle question to
learn about one it does not know yet.

**The claim.** Claude builds a picture of the areas of your life, and uses it so its suggestions
fit you rather than a generic user.

**Why.** This is the part with no free-tier equivalent at all, and it is the hardest to convey with
screenshots, which is exactly why it needs a real exchange.

**Note for filming.** `SYSTEM-PROMPT.md` is explicit that Claude never runs a structured intake or
produces a checklist of life areas to fill in. The filmed exchange must show the gentle in-context
version — one question, asked because something came up — or it will demonstrate the behaviour the
system prompt forbids.

*Live Claude conversation.*

---

## Page 5 — The Strategy doc reconciles (~25s)

**On screen.** The Strategy doc, with the user editing one Project's paragraph to push it out by six
months. Cut to Claude noticing the tasks that now contradict it, presenting them grouped, and
asking what to do. The user answers; the tasks move.

**The claim.** Change your mind about the shape of the year, and Claude finds the work that no
longer fits and asks you about it.

**Why this is last and longest.** It is the deepest thing the paid tier does and the least
guessable from the outside. A viewer who understands this page understands the tier.

**Note for filming.** Claude must be shown *asking*, never silently editing — grouped observations,
then a question. Silent edits are forbidden in `SYSTEM-PROMPT.md`, and a video showing them would
promise a product that is not being built.

*Live Claude conversation.*

---

## Page 6 — And without it (~10s)

**On screen.** Taskflow being used plainly: a task captured, dragged from Today to Soon, ticked off.
No Claude anywhere.

**The claim.** All of this works on its own. The free tier is the whole app — you are choosing
whether to add Claude, not whether to have a task manager.

**Why the video ends here.** The choice screen follows immediately, and it should follow an honest
statement of what declining actually costs. Ending on the paid tier's best moment would make "Skip
AI for now" feel like a mistake, which is not what the tier model says it is.

*Screen capture.*

---

## Running order and total

| Page | Content | Kind | Approx. |
|---|---|---|---|
| 1 | Where this happens | screen capture | 10s |
| 2 | The connection, in one line | screen capture | 8s |
| 3 | Claude puts something into Taskflow | live Claude | 20s |
| 4 | Claude knows the shape of your life | live Claude | 22s |
| 5 | The Strategy doc reconciles | live Claude | 25s |
| 6 | And without it | screen capture | 10s |

Total ≈ 95 seconds at the upper end of each estimate.

## What this script does not decide

Visual treatment, voice-over versus captions, music, and whether the six pages are one continuous
video or six separately-advanced cards. Those are production decisions and do not change what the
video says.
