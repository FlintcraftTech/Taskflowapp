# How a Claude custom connector authenticates to a remote MCP server

Looked up 2026-08-25, for [mcp-server-auth-model] — deciding how Taskflow's
remote MCP server ties an incoming request to one user's data.

## What the connector surface accepts

Anthropic's remote-MCP connector surface supports these authentication types:
`oauth_dcr`, `oauth_cimd`, `oauth_anthropic_creds`, `custom_connection`,
`static_headers`, and `none`. The two OAuth discovery flows — DCR (dynamic
client registration) and CIMD — work out of the box, with no per-connector
setup on Anthropic's side.

The current MCP authorization specification is built on OAuth 2.1. For
user-specific data it requires the server to know who authorized a request and
what that identity may reach.

## What the user actually does

On Pro/Max, adding a custom connector is: Customize → Connectors → "+" → "Add
custom connector" → paste the server URL → Add. An "Advanced settings" panel
optionally takes an OAuth Client ID and Client Secret. The consumer flow then
runs an OAuth sign-in against the server, where the user reviews the
permissions being requested.

So the only thing a consumer user is asked to paste is a URL. There is no
consumer-facing field for pasting an API key. `static_headers` exists, but the
published guidance frames it as fitting a fixed organisation-level credential,
not a per-user one.

## The explicit warning

Anthropic's guidance says not to put credentials in the connector URL: a URL
carrying a token leaks through server logs, proxy logs, browser history,
analytics, screenshots and support tickets. Credentials belong in OAuth or in
request headers.

## What this settles for Taskflow

A per-user secret in the URL is ruled out by the warning above. A pasted static
key has no consumer-facing field to paste into. That leaves OAuth — with DCR as
the route needing no registration with Anthropic — as the only shape that fits
a consumer app where every user's data is their own.

## Sources

- https://support.claude.com/en/articles/11175166-get-started-with-custom-connectors-using-remote-mcp
- https://sunpeak.ai/blogs/claude-connector-oauth-authentication/
