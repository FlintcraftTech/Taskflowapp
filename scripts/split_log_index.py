#!/usr/bin/env python3
"""Move each ended month's lines out of LOG/index.md into LOG/index-YYYY-MM.md.

The method's rule is that the current month's index lines stay in LOG/index.md and
every month that has ended lives in its own file, so a planning session's opening
read stays short as the archive grows. Retrieval searches LOG/index*.md, so nothing
is lost to the move.

How a line's month is found, cheapest first:

  1. a YYYY-MM-DD date in the entry filename the line ends with;
  2. a YYYY-MM-DD date anywhere in the line's own text;
  3. the date git records for when that entry file was added
     (`git log --diff-filter=A`).

Many older entries carry no date in their filename — the numbered 00NN-*.md batch
records and other pre-convention ones — which is what blocked an earlier attempt to
do this by hand. Git dates them without opening anything.

Accepted rather than solved: a git add-date is when the entry was committed, which
can be a day after the session it records. For sorting into months that is almost
always the same answer; an entry written within a day of a month boundary could
land in the wrong file.

Re-runnable. A month already split is left alone, and a line whose month cannot be
determined at all stays in index.md and is reported.

Usage:  python scripts/split_log_index.py [project-root]
"""

from __future__ import annotations

import re
import subprocess
import sys
from collections import OrderedDict
from pathlib import Path

DATE_RE = re.compile(r"(\d{4})-(\d{2})-(\d{2})")
# An index line is a bullet; the entry file it names is the last *.md on the line.
ENTRY_FILE_RE = re.compile(r"([^\s/\\]+\.md)\s*$")


def is_entry_line(line: str) -> bool:
    return line.startswith("- ")


def month_from_git(log_dir: Path, filename: str) -> str | None:
    """The month git records for the commit that added this entry file."""
    path = log_dir / filename
    try:
        out = subprocess.run(
            [
                "git",
                "log",
                "--diff-filter=A",
                "--follow",
                "--format=%ad",
                "--date=short",
                "--",
                str(path),
            ],
            cwd=log_dir.parent,
            capture_output=True,
            text=True,
            check=True,
        ).stdout.strip()
    except (subprocess.CalledProcessError, OSError):
        return None
    dates = [d for d in out.splitlines() if d.strip()]
    if not dates:
        return None
    # --follow lists newest first; the add commit is the oldest line.
    match = DATE_RE.search(dates[-1])
    return f"{match.group(1)}-{match.group(2)}" if match else None


def month_of(line: str, log_dir: Path) -> str | None:
    filename_match = ENTRY_FILE_RE.search(line.rstrip())
    if filename_match:
        in_filename = DATE_RE.search(filename_match.group(1))
        if in_filename:
            return f"{in_filename.group(1)}-{in_filename.group(2)}"
    in_line = DATE_RE.search(line)
    if in_line:
        return f"{in_line.group(1)}-{in_line.group(2)}"
    if filename_match:
        return month_from_git(log_dir, filename_match.group(1))
    return None


def current_month(log_dir: Path) -> str:
    """The newest month present in the index — what 'the current month' means here.

    Read off the file rather than off the clock, so a run in a quiet month does not
    empty the index of the most recent lines anyone is still reading.
    """
    months = []
    for line in (log_dir / "index.md").read_text(encoding="utf-8").splitlines():
        if is_entry_line(line):
            month = month_of(line, log_dir)
            if month:
                months.append(month)
    return max(months) if months else ""


def main(root: Path) -> int:
    log_dir = root / "LOG"
    index = log_dir / "index.md"
    if not index.exists():
        print(f"split_log_index: no {index}", file=sys.stderr)
        return 1

    lines = index.read_text(encoding="utf-8").splitlines()
    keep_month = current_month(log_dir)
    if not keep_month:
        print("split_log_index: no dated lines found; nothing to do")
        return 0

    kept: list[str] = []
    moving: "OrderedDict[str, list[str]]" = OrderedDict()
    undated = 0

    for line in lines:
        if not is_entry_line(line):
            kept.append(line)
            continue
        month = month_of(line, log_dir)
        if month is None:
            undated += 1
            kept.append(line)
        elif month >= keep_month:
            kept.append(line)
        else:
            moving.setdefault(month, []).append(line)

    if not moving:
        print(f"split_log_index: nothing to move; index.md holds {keep_month} only")
        return 0

    for month, month_lines in moving.items():
        target = log_dir / f"index-{month}.md"
        header = [
            f"# LOG Index — {month}",
            "",
            f"Index lines for sessions in {month}, newest first, moved out of "
            "`index.md` when the month ended. Same format as `index.md`; a "
            "retrieve searches `LOG/index*.md`.",
            "",
        ]
        existing = []
        if target.exists():
            existing = [
                line
                for line in target.read_text(encoding="utf-8").splitlines()
                if is_entry_line(line)
            ]
        merged = existing + [line for line in month_lines if line not in existing]
        target.write_text("\n".join(header + merged) + "\n", encoding="utf-8")
        print(f"split_log_index: {len(month_lines)} line(s) -> {target.name}")

    index.write_text("\n".join(kept) + "\n", encoding="utf-8")
    print(f"split_log_index: index.md now holds {keep_month}")
    if undated:
        print(
            f"split_log_index: {undated} line(s) left in place — no date in the "
            "filename, in the line, or in git"
        )
    return 0


if __name__ == "__main__":
    raise SystemExit(main(Path(sys.argv[1] if len(sys.argv) > 1 else ".").resolve()))
