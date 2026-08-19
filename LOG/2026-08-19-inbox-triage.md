# [HASH] — Mail triage: a consumer project's report on multi-part `[user]` work became the capture [method-user-item-to-taskflow-handoff]

The chat opened with one message waiting in the mailbox and closed on it; no queue was processed and nothing was built.

The message came from a consumer project running this method. It reported a real failure of the `[user]` work item shape: such an item carries a walkthrough and is walked through live, which works for a handful of steps in one sitting, and broke on an item whose first step turned out to contain an extraction, a re-sort and a per-pile filing decision of unknown length, spread over days and partly belonging to different work entirely. Written as one queue line, work like that hides its own size; split into many lines it floods a queue that exists to track a venture rather than a person's errands. The user's conclusion, carried in the message, is that those parts belong on their to-do list — which is this app. That makes it Taskflow design work rather than a method complaint, so it routed to Unprocessed as a single capture rather than to the feedback channel.

Two things in the message were deliberately kept in the capture rather than summarised away: that the handoff most likely fires mid-walkthrough, at the moment the item's true size becomes visible and the user is least able to stop and reorganise a queue, and that the open questions are Taskflow's own — what a handed-off task looks like, whether items travel one way or round-trip, and what the queue keeps once every part is done. The sending project is described generically in the capture, since the mailbox is gitignored and the queue is committed.

Not settled here: the message also asked that Taskflow take the subject up with the method project directly. That is an outbound message and waits on the user.

**Queue changes:** one capture appended to Unprocessed — [method-user-item-to-taskflow-handoff]. The cleared-to-run marker and the Processed order were left exactly as the setup migration placed them; nothing this session touched them.

**Work processed:** none. Filing is open to any chat; deciding this capture's fate is /plan's.

**Also in this chat:** the tree opened dirty with the previous session's tail — its hash backfill into that entry's heading and index line, and the `[forward-advisory]` line it left at the top of Unprocessed pointing at [claude-md-stale-vocabulary]. Both folded into this commit, as the one-commit-per-session shape intends. The advisory is left standing for the next /plan to read and clear.
