-- 0003_grants.sql
-- Table privileges for the authenticated role on the four tables created in 0001.
--
-- Row Level Security and table privileges are two separate gates, and Postgres
-- checks the privilege first. A policy says which rows a role may touch; it does
-- not give the role permission to touch the table at all. 0001 issues no grant
-- statements and 0002 only creates policies, so a signed-in Taskflow user's first
-- real query fails with "42501: permission denied for table tasks" before any
-- policy is consulted. This file closes that gap.
--
-- Four decisions are recorded here so they are not re-opened:
--
--   * A NEW migration, not an edit to 0001 or 0002. Both have already been
--     applied to the live project. Editing an applied migration makes the file
--     stop describing what actually ran, and a fresh project would then be built
--     by a sequence nobody has exercised.
--
--   * Per-table grants, written out. Refused: `alter default privileges` on the
--     schema. Default privileges reach only tables created after they are set, so
--     they would do nothing for four tables that already exist and the explicit
--     grants would be needed anyway — and they would silently open future tables,
--     which is the opposite of the closed-by-default posture 0001 takes.
--
--   * `grant` is idempotent in Postgres, so this file is safe to run repeatedly.
--     That is also what makes it the repair for the live database rather than a
--     change to it: the grants are already there by hand, applied during the
--     2026-09-06 drive, so applying this file proves the file and the project
--     agree.
--
--   * Refused: adding `if not exists` guards to 0001 and 0002. A migration series
--     is applied once in order, so re-running an applied file is not something to
--     make safe — doing so invites it. On an empty project both run cleanly, which
--     is the case that matters.
--
-- Nothing is granted to the anon role, matching 0002's stance that a request with
-- no session matches no policy at all. No sequence grants: every primary key is a
-- UUID default, so there is no sequence to grant on.

grant select, insert, update, delete on public.projects          to authenticated;
grant select, insert, update, delete on public.tasks             to authenticated;
grant select, insert, update, delete on public.strategy_entries  to authenticated;
grant select, insert, update, delete on public.life_areas        to authenticated;
