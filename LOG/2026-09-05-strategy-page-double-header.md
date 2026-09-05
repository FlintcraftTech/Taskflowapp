# [HASH] — /plan [strategy-page-double-header]: the spine header gains a trailing action slot, which is where the Strategy share button goes

Session date and time: 2026-09-05, afternoon planning run.

The capture had already worked out the easy half — the Strategy screen's own back arrow and title
duplicate the spine header above them, and can simply go, because the spine header names the page and
the arrow was wired to return to Today, which is exactly the system back gesture's behaviour under the
spine rule. What it correctly flagged as a design question was the share button: removing the row means
finding somewhere else for it.

Reading the code turned up the constraint the capture suspected but could not name. The spine header's
end corner was deliberately left clear at the original build for the drag-to-delete target SPEC
§Drag-target icons puts in the upper-right. That reservation is real — and it does not bite here,
because the delete target appears while a task is being dragged and the Strategy page holds headings and
paragraphs rather than tasks. On the one page that would use the slot, the corner is never claimed.

Three options were put to the user: a trailing action slot in the spine header, empty everywhere but
Strategy; leaving the share button in a thinned-down row of its own; or a share control at the foot of
the document. They chose the first. Refused: the thinned row, which is the smallest change and still
leaves two header rows stacked, which is the thing being fixed; and the foot-of-document control, which
hides the app's one sharing affordance below a scroll of the user's own writing.

One mechanism was settled rather than left to the build. The Strategy screen builds its share intent
from a view-model it obtains itself, so a header outside that screen cannot reach it. The view-model
moves up into the screen that hosts the pager, and the sections pass down as a parameter — ordinary
state hoisting, named in the item so the build does not have to invent a route.

No SPEC edit was owed: SPEC §Strategy doc says a share button in the Strategy doc area shares the doc
through Android's share sheet, which stays true. Where the button sits is UI mechanics, which SPEC's
intro excludes by design.

**Queue changes:** [strategy-page-double-header] rewritten with its file list and moved to Processed,
cleared to run.

**Work processed:** kept — [strategy-page-double-header].
