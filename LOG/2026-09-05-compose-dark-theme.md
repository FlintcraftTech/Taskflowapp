# [HASH] — Taskflow gains light and dark palettes and follows the phone's setting

Date: 2026-09-05 11:07

Taskflow had no colour scheme of its own, so it rendered light on every phone whatever the phone was
set to. The user confirmed it against their own device on 2026-09-03: their phone is in dark mode
and the app was always white.

The mechanism, read during planning: `MainActivity` called `MaterialTheme { AppRoot() }` with no
colour scheme argument, so Compose fell back to its built-in **light** palette regardless of the
phone. The Android window underneath *did* follow the system, so on a dark phone the window was dark
while everything Compose painted on it was light — a light screen in a dark frame where a screen
painted its own background, and unreadable near-black text on the dark window where it painted none.
That second case is what made the onboarding screens illegible on 2026-09-02.

What made this cheap is the fact the decision turned on: a grep found **zero** hardcoded colours
across the app's Kotlin files. Every colour already resolved through `MaterialTheme.colorScheme`, so
defining the schemes was the whole job and no screen needed touching. That still held at build time
— a re-grep found none outside the new theme package.

Whether Taskflow should have a dark theme at all was a product question, settled by the user: follow
the system. The reason given was that a phone in dark mode at 1 AM is exactly the person
SPEC §Settings → Day begins at exists for, and a task app that answers them with a full white screen
fails them. Light-only was refused on that ground, and an in-app appearance setting was refused
because the phone already holds that preference.

**One alternative was weighed during the build and lost.** Material You dynamic colour was
considered for the two schemes and rejected: the palette is chosen for a calm surface at 1 AM — a
quiet near-black rather than pure black, an off-white rather than a bright white — and a
wallpaper-derived scheme hands that decision to whatever picture the user has set.

Files touched:
- `app/src/main/java/com/example/taskflow/ui/theme/Color.kt` — new; the light and dark palette values
- `app/src/main/java/com/example/taskflow/ui/theme/Theme.kt` — new; `TaskflowTheme`, choosing between
  the schemes with `isSystemInDarkTheme()`
- `app/src/main/java/com/example/taskflow/MainActivity.kt` — `MaterialTheme { }` becomes
  `TaskflowTheme { }`

Routed to Captures: none.

Tick: done, UNCONFIRMED — needs a device with the phone in dark mode, then in light mode, checking
every screen including onboarding, which paints no background of its own and so fails first.
