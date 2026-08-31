# [HASH] — build [disable-drawer-swipe-open]: drawer swipe-to-open disabled by gating gestures on the drawer being open

The item asked for a flat `gesturesEnabled = false` on the ModalNavigationDrawer. The building session read Material3 1.4.0's NavigationDrawer.kt from the Gradle cache rather than guessing, and found the scrim's tap-to-close hangs off the same flag (line 375: `if (gesturesEnabled && ...)`) — so a flat false would have broken the item's own acceptance criterion that a scrim tap still closes the drawer. It gated on `drawerState.isOpen` instead: closed → no gesture can open it; open → scrim tap and drag-to-close behave normally. An alternative seriously weighed, and why the item's literal instruction lost: the instruction predated reading the library source.

Shipped UNVERIFIED on two counts: the change never compiled (Gradle's client-to-daemon loopback connection fails on this machine for Claude, retried and confirmed at this close), and the on-device gesture checks were never run — those are filed as the `[user]` capture [verify-drawer-swipe-off-on-device].

This entry was written at the close of the build by a later session: the building session crashed before running /done, and its build working file carried the record this entry is authored from.

**Changes:** app/src/main/java/com/example/taskflow/ui/navigation/AppRoot.kt — `gesturesEnabled = drawerState.isOpen` added to the ModalNavigationDrawer, with a comment recording why it is gated rather than flat false.

**Tests:** none run — compile impossible in the building environment; on-device acceptance checks filed as [verify-drawer-swipe-off-on-device].
