# [HASH] — /next close 2026-09-12: what this chat did outside its work items

Written 2026-09-12 at 13:14, read from the clock. The session's twelve work items have their own entries; this one carries what belongs to no item.

**Also in this chat:**

- **Alex approved adding `ui/common/Reorderable.kt` to the run's scope**, mid-build, so the Strategy doc's Project heading could be the drag handle rather than the whole section. The reasoning is in [project-reorder-strategy]'s entry.

- **She gave explicit consent for adb against her connected phone**, once, covering the two device audits and the fresh install they needed. The rollover walkthrough's setup steps and the Day begins at restoration were each asked for separately, because each changed her live data or settings rather than only reading them.

- **One compile was handed over covering four code items rather than one per item.** The run recorded why in its working file and left all four in the queue until `BUILD SUCCESSFUL` came back, so nothing shipped on an unverified build. That is a departure from the literal wording of the rule written in this same session, and it is filed as [compile-rule-vs-multi-item-runs] rather than left as a silent precedent.

- **Two environment facts were found and written into `TOOLS.md` in the moment.** A fresh `assembleDebug` wrote the debug APK to `outputs/` and left the copy at the `intermediates/` path TOOLS.md recorded a week stale — installing from the recorded path would have put old code on the phone while every check read as a test of the new build. And typing into an app text field over adb needs one character at a time with a read-back, after a whole sentence arrived as four characters.

- **The credential-shape scan flagged the same two lines at every queue write**, an email address appearing twice in QUEUE.md's Unprocessed section. It was mentioned at the run's opening and left alone: it is the app's own bug-report address, which SPEC §Side menu says the Report a bug row hands to the user, so its presence in planning prose is deliberate rather than a leak. Stated here so the repeated flag is not re-investigated next session. That scan matches credential shapes and nothing else — it cannot tell whether prose names a real person or a real case.

- **A correction given twice, worth recording once:** two messages were stopped for using "today" and "tomorrow" with no source. Both were page names in Taskflow rather than claims about dates, and the records now say so explicitly where those words appear.

**Routed to Captures:** [supabase-apply-0003-grants], [strategy-paragraph-input-loses-characters], [tomorrow-task-lands-on-today-under-custom-boundary], [verify-2026-09-12-code-on-device], [run-migration-test-on-emulator], [compile-rule-vs-multi-item-runs].

**Advisory:** filed — [verify-2026-09-12-code-on-device]
