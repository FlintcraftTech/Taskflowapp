# Sharing a day's tasks from Android — PNG vs PDF vs plain text

Looked up 2026-08-20, for [nav-completed-history]'s share-a-day sub-question.

## What is settled

Android's share sheet is driven by MIME type. `ACTION_SEND` with a `content://`
URI in `EXTRA_STREAM` and an exact type — `image/png` or `application/pdf` —
determines which apps appear as share targets. There is no format Android
treats as privileged; the sheet simply filters to apps that declare they handle
that type. Both PNG and PDF are handled by the ordinary destinations a person
would share a day to: messaging apps, email, notes, cloud storage.

So compatibility does not decide this. That was the assumption behind the
item's original "PNG is most universally readable" lean, and it is not wrong so
much as no longer the deciding factor.

## What actually differs

The difference is what happens on arrival, not whether it arrives.

- **PNG** renders inline. In a chat thread the recipient sees the day without
  tapping anything, and the image sits in the conversation as content.
- **PDF** arrives as a document attachment. The recipient taps it and a viewer
  opens. It prints more predictably and holds selectable text.
- **Plain text** is the most portable and the least controlled — it renders as
  whatever the receiving app does with a wall of lines, with no layout at all.

For a single day's completed tasks — a short list, read once, usually sent to a
partner or a friend rather than filed — inline rendering is the behaviour that
matches the use. PDF's advantages (printing, text selection, pagination) are
advantages for documents that get kept.

## Markdown as the text format (checked 2026-08-21)

Sharing the day as Markdown rather than unstructured text was chosen. The
question was which MIME type carries it.

`text/markdown` is a registered type, but mainstream Android apps do not
declare it in their intent filters — an app appears as a share target only if
its manifest carries `<data android:mimeType="text/markdown"/>`, and
essentially none do. Declaring it therefore produces a near-empty share sheet.

The documented approach is `text/plain`: a Markdown file is plain text, so the
type is honest, the full set of text-handling targets appears, and the
structure survives in the message body for the recipient to read or paste
onward. Android's own guidance is to use the most specific type that is
actually true of the data, and to avoid `*/*` because most receiving apps
cannot handle arbitrary content.

Consequence for the build: share Markdown under `text/plain`. Do not declare
`text/markdown`, and do not reach for `*/*` to widen the target list.

## The limit of this finding

No source was found that compares real-world acceptance of PNG against PDF
across specific destination apps, and none was found that is specific to 2026.
The MIME-type mechanics come from Android's own developer documentation and are
stable; the arrival-behaviour comparison above is reasoning from how those
formats are handled generally, not a measured result. If the format choice ever
turns out to matter more than expected, this is the part to re-check rather
than trust.

Sources: Android developer documentation on sending simple data to other apps;
CodePath's sharing-with-intents guide.
