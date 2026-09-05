# [HASH] — Deleted after its content moved: the end-to-end test's walkthrough now opens on a JSON export

Recorded 2026-09-06, 00:29.

Alex raised this on 2026-09-05, asking whether she can count on tasks staying in the app and saying she
cannot truly test it without feeling at home in it. The capture's answer was one line added to
[first-end-to-end-test]'s walkthrough, before its old step 1: take a JSON export and keep the file off the
phone.

That is a queue edit rather than a build, so it was made here and the capture deleted with its content
moved. The test item's walkthrough now runs to seven steps, opening on the export with a look-for that
names the file where she put it rather than a success message.

The paragraph written above the walkthrough keeps the capture's care about what is and is not
established. Established: `MigrationTest` passed for the first time on 2026-09-05, so the version-5 floor
is demonstrated rather than claimed; export and import round-tripped on the device the same day; and when
the app was uninstalled that evening, Android Auto Backup restored the data by itself. Not established,
and added at this decision step because the capture was written before it was known: Auto Backup's
success was unplanned rather than a designed recovery path. That fact cut both ways and both halves are
written down — it makes the item less bleak than the capture implied, and it is not a reason to skip the
export.

**Queue changes:** [first-end-to-end-test]'s walkthrough gained a first step and a preceding paragraph;
this capture deleted.
**Work processed:** deleted — [export-before-first-end-to-end-test], content relocated.
