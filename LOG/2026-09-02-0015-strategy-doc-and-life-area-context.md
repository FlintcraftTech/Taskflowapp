# [HASH] — build [0015-strategy-doc-and-life-area-context]: the free-tier Strategy doc, with structure generated and only the paragraphs written

SPEC §Strategy doc makes the doc's structure **mechanically generated** — one heading per Project, from the Project's name, in Project order, with the user writing only the paragraph beneath each. That is what lets a free-tier user have a coherent document without Claude in the loop: there is no document to lay out, only descriptions to write.

Headings are therefore not editable here. They are Project names, and renaming an area of life is not something to do by typing over a heading in a document. The system Unassigned Project is excluded, since it is not a real area of life — only the home for tasks nobody has filed.

The section list is driven by the **Project** list rather than the entry list, so a Project with nothing written about it still gets a heading and an empty paragraph. A new Project appears in the doc the moment it exists, rather than when someone happens to write about it.

The share button hands the doc to Android's standard share sheet as markdown. SPEC's reason is that sharing the strategic picture with the people in the user's life is part of what gives a Strategy doc its function.

**The life-area schema ships with no user-facing surface at all**, which is deliberate rather than incomplete. SPEC and `SYSTEM-PROMPT.md` both put life areas outside the UI entirely — they are an abstract framing layer Claude carries on the paid tier, reached only through MCP tools. The table exists on the free tier and simply stays empty: a schema is cheaper to carry than a migration later, and nothing on a free-tier device writes to it. Its notes column is prose rather than fields, because what makes an area of someone's life is not a shape a schema can anticipate and the one consumer is a language model.

Reordering Projects by dragging headings is not here — that is [project-reorder-strategy], held separately — and neither is any AI reconciliation, which is [0021-strategy-doc-reconciliation-paid-tier].

**Files touched:** app/src/main/java/com/example/taskflow/data/model/LifeArea.kt (new), data/local/LifeAreaDao.kt (new), data/local/TaskflowDatabase.kt (LifeArea registered, schema 4 → 5), ui/strategy/StrategyViewModel.kt (new — sections driven by the Project list, markdown render for sharing), ui/strategy/StrategyScreen.kt (new — generated headings, editable paragraphs, share sheet), ui/navigation/AppRoot.kt (Strategy opens the real screen).

**Routed to Captures:** none.

**Tick:** done, UNCONFIRMED: needs an Android Studio build and the device check (edit a description and it persists across relaunch; rename or reorder a Project and the headings follow; headings cannot be edited directly; the share button opens Android's share sheet).
