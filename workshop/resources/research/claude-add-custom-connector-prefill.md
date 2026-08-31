# claude.ai add-custom-connector — deep-link & pre-fill support

**Question (from planning, 2026-06-16):** Does claude.ai's add-custom-connector URL accept query-param pre-fill for connector name and URL, so Taskflow's AI-setup flow (batch 0019) can deep-link with Taskflow's MCP details already populated?

## Finding

- **Opening the modal via deep link: supported.** Navigating to `https://claude.ai/settings/connectors?modal=add-custom-connector` auto-opens the "Add custom connector" dialog.
- **Pre-filling Name + Remote MCP Server URL: NOT supported (as of 2026-06).** This is an open feature request — [anthropics/claude-ai-mcp Issue #74](https://github.com/anthropics/claude-ai-mcp/issues/74), labelled "enhancement," requesting `mcpName` and `mcpServerUrl` query params. No maintainer response or linked PR; not implemented.
- Proposed (not live) format: `https://claude.ai/settings/connectors?modal=add-custom-connector&mcpName=Server%20Name&mcpServerUrl=https%3A%2F%2Fmcp.myserver.ai%2Fmcp`

## Implication for Taskflow (batch 0019 — AI choice flow and MCP setup)

- The AI-setup flow can deep-link to **open** the modal, but must then **display Taskflow's connector name and remote MCP server URL for the user to copy-paste manually** — auto-population isn't available.
- Optional: watch Issue #74. If `mcpName`/`mcpServerUrl` ship, the flow can upgrade to a fully pre-filled deep link.

## Sources

- [Support query params for modal=add-custom-connector (name, url) · Issue #74 · anthropics/claude-ai-mcp](https://github.com/anthropics/claude-ai-mcp/issues/74)
- [Get started with custom connectors using remote MCP — Claude Help Center](https://support.claude.com/en/articles/11175166-get-started-with-custom-connectors-using-remote-mcp)

*Checked 2026-06-16.*
