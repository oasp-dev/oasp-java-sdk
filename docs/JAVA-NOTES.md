# JAVA-NOTES

What building this SDK in Java 21 forced us to decide — the notes a reader from
another language (the reviewer came from C#) will find least obvious.

## Sealed hierarchies → a closed set → an `Unknown*` fallback
`Resource`, `Event`, and the session-resource union are **`sealed` interfaces**.
Sealing gives a compiler-checked closed set: a `switch` over the type needs no
`default`, and adding a case without handling it is a *compile* error. Java's
`sealed` is unrelated to C#'s `sealed` (which = Java's `final`); the C# analogue
of exhaustiveness doesn't exist.

The tension: a closed set fights forward-compatibility. The resolution — mirrored
from FHIR's `resourceType` posture, now normative in `oasp-standard` — is a
permitted **`UnknownResource`/`UnknownEvent`** fallback: an unrecognised
`resourceType`/`type` from a newer server deserialises *there* (preserving the
raw payload) instead of throwing. Closed and exhaustively-checkable, yet an older
client survives a newer server.

## Records + compact constructors, not builders
Protocol types are immutable `record`s with validation in **compact
constructors** — an invalid instance can't exist. No builders (nothing exceeds a
handful of components). Lists are defensively copied and unmodifiable.
Inbound server data is validated **leniently** (non-null on required fields, no
blank checks) so a stricter-than-spec client can't reject a valid response;
outbound/our-own identifiers are validated strictly.

## Nullable → `Optional<T>` accessor (which forces the component's type)
A record's accessor must return its component's exact type, so you cannot have an
`Instant closedAt` component *and* an `Optional<Instant> closedAt()` method. To
expose an `Optional` accessor the **component itself** is `Optional<…>`, with the
constructor tolerating `null`. (Where a field is simply absent on the wire, JSON
maps absent/`null` ⇄ `Optional.empty()`.)

## Unchecked, sealed exception hierarchy
`OaspException extends RuntimeException` — no checked-exception ceremony on an
HTTP-backed SDK. It's `sealed` with `final` subclasses (Java requires each
permitted subclass to be `final`/`sealed`/`non-sealed`). The catch-all
`OaspProtocolException` carries the raw error envelope so error codes the SDK
doesn't yet model still surface intact.

## Enums carry their wire value
Enum constants don't match the wire strings (`USER` ⇄ `"user"`,
`CREATE_CONVERSATION` ⇄ `"createConversation"`), so each enum carries a
`wireValue()`/`fromWire()` pair instead of relying on `name()`/`valueOf()`.

## Hand-rolled zero-dependency JSON, kept honest by a drift test
The JDK ships no JSON parser and `oasp-client` must add no dependency (a hard
SDK constraint — forcing a JSON-library version on consumers causes diamond
conflicts). So JSON is hand-rolled over the tiny closed type surface, behind a
`ServiceLoader` seam that lets a library-backed codec be swapped in later with no
API change. See `DECISIONS.md` D1.

Because the SDK once drifted from the spec (an assumed conversation lifecycle
that didn't exist), the types are hand-written **and** guarded by a
`SpecDriftTest` that validates every record against `oasp-standard`'s published
JSON Schema — it fails on any missing/extra field, wrong `resourceType`, or wrong
enum value. Generate-to-verify, not generate-to-author (`DECISIONS.md` D3).

## Streaming with zero dependencies
`GET /sessions/{id}/events` is `text/event-stream` (SSE). It's parsed into a
lazy, closeable `Stream<Event>` with the JDK only — closing the stream (or a
try-with-resources) closes the HTTP body, so an early-stopping caller doesn't
leak the connection.
