-- 0002_rls_policies.sql
-- Row Level Security policies for the four tables created in 0001.
--
-- Each table gets four policies — select, insert, update, delete — scoped
-- TO authenticated and matching user_id against the id of the account the
-- request's token belongs to. Nothing is granted to the anon role, so a request
-- carrying no session matches no policy at all.
--
-- Every policy is written as a plain conjunction of an explicit not-null check
-- and an equality. For a caller with no session auth.uid() is null, and a
-- comparison against null evaluates to null rather than false; phrased as a
-- negation or an OR that can widen to every row instead of none. There is no
-- negation and no OR anywhere below, and the not-null check is stated rather
-- than relied on implicitly, so the no-session case denies twice over.
--
-- An UPDATE carries both USING (which rows may be updated) and WITH CHECK (what
-- the row may become), so a row cannot be updated out of its owner's account.

-- projects ------------------------------------------------------------------

create policy "projects_select_own" on public.projects
    for select to authenticated
    using ((select auth.uid()) is not null and user_id = (select auth.uid()));

create policy "projects_insert_own" on public.projects
    for insert to authenticated
    with check ((select auth.uid()) is not null and user_id = (select auth.uid()));

create policy "projects_update_own" on public.projects
    for update to authenticated
    using ((select auth.uid()) is not null and user_id = (select auth.uid()))
    with check ((select auth.uid()) is not null and user_id = (select auth.uid()));

create policy "projects_delete_own" on public.projects
    for delete to authenticated
    using ((select auth.uid()) is not null and user_id = (select auth.uid()));

-- tasks ---------------------------------------------------------------------

create policy "tasks_select_own" on public.tasks
    for select to authenticated
    using ((select auth.uid()) is not null and user_id = (select auth.uid()));

create policy "tasks_insert_own" on public.tasks
    for insert to authenticated
    with check ((select auth.uid()) is not null and user_id = (select auth.uid()));

create policy "tasks_update_own" on public.tasks
    for update to authenticated
    using ((select auth.uid()) is not null and user_id = (select auth.uid()))
    with check ((select auth.uid()) is not null and user_id = (select auth.uid()));

create policy "tasks_delete_own" on public.tasks
    for delete to authenticated
    using ((select auth.uid()) is not null and user_id = (select auth.uid()));

-- strategy_entries ----------------------------------------------------------

create policy "strategy_entries_select_own" on public.strategy_entries
    for select to authenticated
    using ((select auth.uid()) is not null and user_id = (select auth.uid()));

create policy "strategy_entries_insert_own" on public.strategy_entries
    for insert to authenticated
    with check ((select auth.uid()) is not null and user_id = (select auth.uid()));

create policy "strategy_entries_update_own" on public.strategy_entries
    for update to authenticated
    using ((select auth.uid()) is not null and user_id = (select auth.uid()))
    with check ((select auth.uid()) is not null and user_id = (select auth.uid()));

create policy "strategy_entries_delete_own" on public.strategy_entries
    for delete to authenticated
    using ((select auth.uid()) is not null and user_id = (select auth.uid()));

-- life_areas ----------------------------------------------------------------

create policy "life_areas_select_own" on public.life_areas
    for select to authenticated
    using ((select auth.uid()) is not null and user_id = (select auth.uid()));

create policy "life_areas_insert_own" on public.life_areas
    for insert to authenticated
    with check ((select auth.uid()) is not null and user_id = (select auth.uid()));

create policy "life_areas_update_own" on public.life_areas
    for update to authenticated
    using ((select auth.uid()) is not null and user_id = (select auth.uid()))
    with check ((select auth.uid()) is not null and user_id = (select auth.uid()));

create policy "life_areas_delete_own" on public.life_areas
    for delete to authenticated
    using ((select auth.uid()) is not null and user_id = (select auth.uid()));
