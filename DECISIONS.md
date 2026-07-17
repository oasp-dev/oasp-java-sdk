# Decisions

This file records notable technical decisions as they're made, so the reasoning
behind them isn't lost once the code that motivated them has moved on. Issue
#12 will turn this into proper, polished documentation; until then it's kept
as a running, dated log.

## 2026-07-18: Hand-rolled, zero-dependency JSON codec

**Decision:** `oasp-client` serialises and deserialises the OASP protocol
types (issue #5) with a small, hand-rolled JSON implementation
(`dev.oasp.client.json`) rather than a library like Jackson or Gson.

**Why:** `oasp-client` promises zero external runtime dependencies (see §2 of
the project's guiding principles, enforced by the `verifyZeroRuntimeDependencies`
Gradle task). Jackson/Gson would violate that promise. The set of types that
need JSON mapping is small and closed - the protocol types in
`dev.oasp.client.types` - so an explicit, per-type hand-written mapper is a
reasonable amount of code to own, and is arguably safer than a generic
reflective mapper: every mapping is visible in one file
(`HandRolledJsonCodec`), and the compiler flags a missing case.

**The escape hatch:** a `JsonCodec` service-provider interface, resolved via
`ServiceLoader` (`JsonCodec.getDefault()`), sits between callers and the
actual implementation. If the hand-rolled codec ever proves too costly to
maintain, a Jackson-backed `JsonCodec` provider can be dropped in later as an
additional jar on the classpath - this is the fallback rule referenced in §6
- with no change to `oasp-client`'s public API, since every call site already
goes through `JsonCodec`, never through `HandRolledJsonCodec` directly.

**Trade-off accepted:** we maintain a small amount of parsing and mapping
code ourselves (a recursive-descent JSON parser, string escaping, and one
explicit mapping per protocol type) instead of depending on a mature,
battle-tested library.
