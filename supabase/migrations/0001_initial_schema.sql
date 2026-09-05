-- 0001_initial_schema.sql
-- Taskflow's four cloud tables, mirroring the Room schema at version 5
-- (app/schemas/com.example.taskflow.data.local.TaskflowDatabase/5.json).
--
-- Two differences from the local schema, both deliberate:
--   * the primary key is a UUID, not an autoincrementing integer. Two devices on
--     one account would both generate local id 1, so the local key cannot be the
--     cloud key. Mapping local ids to these UUIDs is the sync build's work.
--   * every table carries a user_id referencing the authenticated account. It is
--     what every Row Level Security policy in 0002 matches against.
--
-- Row Level Security is enabled on each table here and no policy is created here.
-- That combination denies every read and write, including the app's own, until
-- 0002 runs. That is the intended order: the tables are closed from the moment
-- they exist.

create extension if not exists "pgcrypto";

-- projects ------------------------------------------------------------------

create table public.projects (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references auth.users (id) on delete cascade,
    name        text not null,
    sort_order  integer not null,
    is_system   boolean not null default false,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now()
);

create index projects_user_id_idx on public.projects (user_id);

alter table public.projects enable row level security;

-- tasks ---------------------------------------------------------------------

create table public.tasks (
    id                           uuid primary key default gen_random_uuid(),
    user_id                      uuid not null references auth.users (id) on delete cascade,
    title                        text not null,
    notes                        text not null default '',
    project_id                   uuid not null references public.projects (id) on delete cascade,
    date                         bigint,
    slot                         text,
    parent_id                    uuid references public.tasks (id) on delete cascade,
    is_completed                 boolean not null default false,
    completed_at                 bigint,
    project_suggestion_declined  boolean not null default false,
    slot_sort_order              integer not null default 0,
    project_sort_order           integer not null default 0,
    recurrence                   text,
    completed_instances          text not null default '',
    created_at                   timestamptz not null default now(),
    updated_at                   timestamptz not null default now()
);

create index tasks_user_id_idx on public.tasks (user_id);
create index tasks_project_id_idx on public.tasks (project_id);
create index tasks_parent_id_idx on public.tasks (parent_id);

alter table public.tasks enable row level security;

-- strategy_entries ----------------------------------------------------------

create table public.strategy_entries (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references auth.users (id) on delete cascade,
    project_id  uuid not null references public.projects (id) on delete cascade,
    content     text not null,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now()
);

create index strategy_entries_user_id_idx on public.strategy_entries (user_id);
create index strategy_entries_project_id_idx on public.strategy_entries (project_id);

alter table public.strategy_entries enable row level security;

-- life_areas ----------------------------------------------------------------

create table public.life_areas (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references auth.users (id) on delete cascade,
    name        text not null,
    notes       text not null default '',
    sort_order  integer not null,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now()
);

create index life_areas_user_id_idx on public.life_areas (user_id);

alter table public.life_areas enable row level security;
