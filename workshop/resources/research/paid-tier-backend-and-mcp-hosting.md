# Where Taskflow's paid tier could run — sync backend and MCP server hosting

Researched 2026-09-03, to settle the question four cleared queue items are stuck
behind: the paid tier has nowhere to run.

## The two jobs, and whether one product does both

Taskflow's paid tier needs two things at once:

1. **A cloud store** the Android app pushes to and pulls from, holding one
   account's tasks, Projects and Strategy doc.
2. **A public MCP endpoint** Claude can reach, which authenticates *Taskflow's
   own users* — not an admin key — and answers each tool call against that one
   account's data.

They are usually treated as separate problems. Two routes came out of the search
that answer them differently.

### Supabase — both jobs in one product

Postgres with a REST and realtime API, plus Supabase Auth, plus Edge Functions
for hosting code. What makes it fit the second job specifically is its **OAuth
2.1 Server**: Supabase documents it as a way to "build your own MCP servers that
connect to your Supabase project" and "authenticate AI agents using your existing
user base", with a `.well-known/oauth-authorization-server` discovery endpoint and
an option to **enable dynamic client registration** so MCP clients register
themselves. That is the exact shape [0020-remote-mcp-server]'s settled auth design
asks for, available as a configuration rather than as a server to write.

Supabase's own documentation carries a caution worth carrying into the design:
dynamic registration lets *any* MCP client register with the project, and it
recommends approval requirements and monitoring.

**Price, read from supabase.com/pricing on 2026-09-03.** Free: $0, 500 MB
database, 50,000 monthly active users, 2 active projects, 5 GB egress — **but
free projects are paused after one week of inactivity**, which is fine while
building and fatal for a live paid tier. Pro: **$25/month**, 8 GB disk per
project, 100,000 MAUs, daily backups kept 7 days, 250 GB egress, includes $10/month
of compute credit covering one Micro instance.

### Cloudflare Workers — purpose-built for the MCP half, assembly for the other

Cloudflare publishes `@cloudflare/workers-oauth-provider`, a library that makes a
Worker act as an OAuth 2.1 provider to MCP clients, handling tokens so the server
code receives an already-authenticated user. Cloudflare has blogged and documented
remote MCP servers as a first-class use case. It is the more purpose-built option
for job 2.

Job 1 is where it costs more work: the sync store would be D1 (SQLite) or another
database, and Taskflow's own user accounts and sign-in would have to be built
rather than configured.

**Price, read 2026-09-03.** Workers Paid: **$5/month** including 10 million
requests and 30 million CPU-ms; D1 at $0.001 per million reads and $1.00 per
million writes. Independent 2026 estimates put a production app combining
Workers, KV, D1 and R2 at roughly $15–50/month.

## The finding that changes an already-settled decision

[0020-remote-mcp-server]'s auth design, settled 2026-08-25, specifies OAuth 2.1
with **dynamic client registration**. That is no longer the preferred shape.

**Client ID Metadata Documents (CIMD)** — where the client's `client_id` is an
HTTPS URL serving a JSON metadata document, so there is no registration step at
all — was adopted by the IETF OAuth working group in October 2025 and made the
**preferred default for MCP client registration in the MCP specification of
2025-11-25** (SEP-991). DCR remains supported as a **fallback**. Cloudflare's own
documentation goes further and describes DCR as deprecated for new
implementations and slated for removal after summer 2027.

The MCP spec's stated priority order for a client that supports everything:
pre-registered client information, then CIMD where the authorization server
advertises `client_id_metadata_document_supported`, then DCR where a registration
endpoint is advertised, then manual entry.

This does not overturn the OAuth decision — OAuth is still the answer, and the
reasoning that ruled out URL-borne and pasted static keys is untouched. What ages
is the registration mechanism named inside it.

## Frame assessment

- **TIME RANGE** — the product is pre-launch with no paid users, so the range that
  matters is "cheap enough to run before anyone pays, and able to grow after".
  Both routes cover it. SPEC states no target user numbers, which is a gap in SPEC
  rather than in this research.
- **PEOPLE** — the decision-maker is a solo no-code developer paying out of pocket,
  who will not be writing a bespoke authorization server. That is what weights
  configuration over assembly here. The end users are Claude subscribers on the
  paid tier.
- **FRESHNESS** — amended on a cycle and moving fast. Prices change; the MCP
  authorization spec changed materially within the last year and has a dated
  removal on the horizon (DCR, after summer 2027). Re-check before building, not
  only before deciding.
- **RISK IF WRONG** — moderate and recoverable on the hosting choice: the Android
  app talks to whatever backend it is pointed at, and no user data exists yet to
  migrate. Higher on the registration mechanism: building DCR-only into a server
  whose spec has already demoted it means rework on a clock. Warrants re-checking
  the MCP authorization spec at the moment [0020-remote-mcp-server] is built.
- **ALTERNATIVES** — Supabase and Cloudflare Workers were compared. Firebase,
  Railway, Fly.io, Render and a self-hosted VPS were **not** researched, and are
  ruled out by nothing here except that neither search surfaced them as MCP-server
  routes. Naming that rather than implying a sweep.
