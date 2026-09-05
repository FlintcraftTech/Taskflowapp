# 9e24ba7 — /plan [side-menu-spine-mismatch]: the menu now mirrors the whole spine, with each new row folded into the page item that makes it work

SPEC §Side menu claimed the menu "mirrors the spine from top to bottom" and then listed five of its
seven pages — true when written, false once the spine grew leftward.

The capture floated leaving Search and Yesterday off, on the grounds that a menu row for Search is odd
when the page is one swipe away. That argument does not survive: Tomorrow is also one swipe from Today
and is in the menu. The line that would work — execution pages only — is broken too, because Strategy is
in the menu and is not an execution page.

What settled it is SPEC's own stated reason for the menu: it "gives one-tap reach to every page on the
spine, in spine order". Leaving two pages off makes that sentence false a second way. Search most of
all, since it is the page someone reaches for when they have lost something, which is exactly when
swiping around hunting is the wrong answer.

Rather than a third item held against the two page items, each row was folded into the item that builds
the page it navigates to — a row pointing at a page that does not exist is the only thing a separate
item could have delivered.

**Queue changes:** [side-menu-spine-mismatch] deleted; a menu row added to [nav-yesterday-page]'s and
[nav-search-completed-history]'s Files lists. SPEC §Side menu rewritten in the same session to list all
seven pages in spine order.

**Work processed:** deleted after folding — [side-menu-spine-mismatch].
