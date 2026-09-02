# [HASH] — build [0014-json-export-and-import]: export, a replacing import and a separately-named additive one — and the completion date SPEC required but the schema lacked

SPEC §JSON export and import exists because portability matters even with no external integrations: free-tier users never get cloud sync, and JSON export gives every user explicit control over their data plus a recovery path beyond Android's Auto Backup.

**The item's design was settled in planning on 2026-08-25 and this build carried it**, including two things it refuses. The additive import is a **separately named action**, not a checkbox or mode on the replacing one, because one of them destroys data and the other cannot — a checkbox beside a destructive action is how people lose their task list. And incoming tasks are **not de-duplicated** against what is already there: a duplicate is an annoyance the user deletes in one gesture, while a wrongly-skipped task is work that silently never arrived.

**The build found SPEC describing a column that did not exist.** SPEC requires every exported task to carry its completion state *and the date it was completed*, a parent's written as the value rolled up from its children. The tasks table had only the completion flag. Exporting a field that does not exist is not an option, so `completed_at` was added and is stamped and cleared alongside the flag — a parent's stamp being the moment its last child was ticked, which is when the parent actually became complete. Schema went to v4 for it.

`org.json` was used rather than a serialization library. The format is three flat arrays, org.json ships with Android, and a plugin plus its codegen would be more machinery than the problem has.

The additive path drops incoming ids on the way in, so an incoming task cannot overwrite an existing row that happens to share its id, and it re-points children at their parents' newly assigned ids. Projects are matched by name and created when missing.

Files are chosen through Android's own document picker, so Taskflow never needs storage permission and the user picks where their data goes.

**Files touched:** app/src/main/java/com/example/taskflow/data/transfer/TaskflowJson.kt (new — the format, with a parent's completion rolled up on the way out), data/transfer/TransferRepository.kt (new — export, replacing import, additive import), data/model/Task.kt (completed_at), data/local/TaskflowDatabase.kt (schema 3 → 4), data/local/TaskDao.kt (updateCompletion stamps/clears completed_at; deleteAll), data/local/ProjectDao.kt (getAllIncludingSystem, deleteAllUserProjects), data/repository/TaskRepository.kt (completion stamping including the roll-up stamp), ui/settings/SettingsScreen.kt (three entries, the document picker, the replace warning), TaskflowApplication.kt (transferRepository).

**Routed to Captures:** none. Related and filed later in the session from the device: [durable-local-data], which notes that this export is currently the only thing standing between a schema change and the loss of everything on the phone — and that it has never been run.

**Tick:** done, UNCONFIRMED: needs an Android Studio build and the device check (export, add and delete some tasks, import the file back and the database matches; the replace warning appears first. Separately, "Add tasks from a file" adds exactly the file's tasks, creating any missing Project, with everything pre-existing still present and no warning).
