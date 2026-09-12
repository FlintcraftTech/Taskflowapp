# d6ac7e8 — /plan [setup-log-entry-malformed-placeholder]: deleted, already done — the placeholder is filled and the opening warning has stopped

The capture existed because every session opening reported that
`LOG/2026-08-25-setup.md` carried a commit-hash placeholder outside hash position,
where the automatic fill could never reach it. Its real argument was about the cost
of a warning that appears every time and is never actioned: it trains everyone to
read past the whole class of opening warnings.

It is fixed. The entry's heading now reads `# 3f06c56 — /setup migration test14 →
test18…`, a real hash in hash position, committed and with nothing pending in the
working tree. The corroborating evidence is this session's own opening, which
carried the routine hash backfill and no housekeeping warning, where the opening of
2026-09-06 named that exact file.

Which session repaired it is not established. The file's last two commits are an
ordinary planning run and the setup run itself, and neither mentions it; most
likely the placeholder was moved into heading position as a side effect of one of
them, after which the automatic fill reached it at the next session start. That is
a guess about the cause and is recorded as one — what is not a guess is that the
placeholder is gone and the warning has stopped.

**Queue changes:** deleted. Nothing else referenced it.

**Work processed:** deleted — [setup-log-entry-malformed-placeholder].
