# Research index

One line per finding: the subject it settles, enough of the finding to decide
whether to open it, and the filename at the end.

- How a Claude custom connector authenticates to a remote MCP server — the connector surface accepts `oauth_dcr`, `oauth_cimd`, `oauth_anthropic_creds`, `custom_connection`, `static_headers` and `none`, with the two OAuth discovery flows working out of the box; the consumer add-connector flow asks the user only for a server URL (OAuth client ID/secret sit behind Advanced settings) and there is no consumer field for pasting an API key, while published guidance explicitly warns against credentials in the URL because it leaks through logs, history and screenshots — which together leave OAuth as the only per-user shape available — claude-custom-connector-auth-options.md
- Sharing a day's tasks from Android — share-sheet targets are filtered by MIME type, so PNG and PDF are equally reachable and compatibility does not decide it; what differs is arrival behaviour (PNG renders inline in a chat, PDF opens as an attachment), which favours PNG for a short read-once day summary; also settles that Markdown must be shared under `text/plain`, since `text/markdown` is declared by almost no Android app and produces a near-empty share sheet — android-share-format-png-vs-pdf.md
