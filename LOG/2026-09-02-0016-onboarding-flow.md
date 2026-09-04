# 3defa43 — build [0016-onboarding-flow]: first run walks the Schedule/Projects split, the video placeholder and a real free-or-paid choice

SPEC §Onboarding — first run exists because the Schedule / Projects split is the structural thing a user has to understand before the app makes sense, and because the AI choice is real: the free tier is a complete product, not a hobbled trial, so the user should see what they are choosing between rather than have it hidden.

Two explainer cards, then the video, then the choice. The **X escape hatch is on every page including the last** — an escape that vanishes when the decision arrives is not an escape — and taking it counts as having seen onboarding, exactly as reaching the end does. Dismissing something is a decision, and re-showing it would be nagging.

The video ships as a placeholder, which the item explicitly allows: what it says is designed in `ONBOARDING-VIDEO-SCRIPT.md`, written in this same run, and filming it waits on the Claude integration existing. The "How do I set up Claude?" path finishes onboarding for now, since the setup flow is [0019-ai-choice-flow-and-mcp-setup], which was dropped from this run — the side menu's AI entry brings the choice back.

The flow's state is held in the view-model rather than recomputed from the settings flow. A single pass through a sequence is not a reactive view of a setting, and treating it as one would make the last page flicker away the instant the flag is written.

**Fixed on the device, 2026-09-02.** On first run the user reported the screens as "dark against dark background". Onboarding returns *before* AppRoot's Scaffold, which is where every other screen gets its background and its matching text colour — so it painted nothing and its text fell back to the default near-black over a dark window. It is now wrapped in a Surface, and the user confirmed it reads correctly. The wider condition it exposed — Compose pinned to its light palette while the Android window theme follows the phone's dark mode — was filed as [compose-dark-theme] rather than fixed, because whether Taskflow has a dark theme at all is a product decision.

**Files touched:** app/src/main/java/com/example/taskflow/ui/onboarding/OnboardingScreen.kt (new — two cards, video placeholder, the choice, the X on every page; later wrapped in a Surface), ui/onboarding/OnboardingViewModel.kt (new — page sequence, finish-on-escape, re-opening the choice), data/settings/SettingsRepository.kt (onboardingSeen), ui/navigation/AppRoot.kt (onboarding gates the app; the "Turn on AI" drawer entry re-opens the choice).

**Routed to Captures:** [compose-dark-theme].

**Tick:** done, UNCONFIRMED: needs an Android Studio build and the device check (a fresh install walks the two cards, the video and the AI choice in order; the X exits from any point and does not re-trigger on next launch; the side-menu entry brings the AI choice back). Ships with the placeholder video the item allows for, since filming waits on the Claude integration. Partially confirmed since: the flow was walked on the device on 2026-09-02, and its legibility fix verified there.
