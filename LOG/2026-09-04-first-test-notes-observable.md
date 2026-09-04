# [HASH] — /plan [first-test-notes-observable]: dropped, after the user pointed out the risk it introduced was the only risk in play

The capture proposed giving [first-end-to-end-test]'s notes a file at a stated path, so the item would
have an observable and nobody would have to remember it was done. The reasoning was sound: that test's
last step stores both the notes and the fact of completion in the user's memory alone, which is none of
the three ways the method knows a `[user]` item is complete.

Claude designed the file into `workshop/resources/testing/` with a `.gitignore` line to go in first,
having flagged that the test deliberately uses real tasks and this repository is public.

The user cut it down: their task data lives in the app's database on the phone, nothing syncs it here,
and the only thing that would have been published is the notes file the proposal itself created. A risk
introduced by the fix, not one that already existed.

What is given up is stated rather than hidden: nothing can tell whether that test has been done except
the user saying so. That is acceptable because they already report findings in chat and the session
writes them into the log, which is a durable record — and [post-first-test-polish-review], held against
that test with those notes as its entire input, can read the log entry rather than a file.

**Queue changes:** [first-test-notes-observable] deleted. [first-end-to-end-test] untouched — its
walkthrough was not edited, since the file it would have named is not being created.

**Work processed:** deleted — [first-test-notes-observable].
