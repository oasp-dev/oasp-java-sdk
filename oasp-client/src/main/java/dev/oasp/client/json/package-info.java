/**
 * A zero-dependency, hand-rolled JSON serialisation layer for {@code
 * dev.oasp.client.types}.
 *
 * <p>This package is deliberately split into two layers that don't know
 * about each other:
 *
 * <ul>
 *   <li>{@link dev.oasp.client.json.JsonWriter} and {@link
 *       dev.oasp.client.json.JsonParser} - the low-level JSON engine. They
 *       only ever deal in JSON's own value model ({@code Map<String,Object>}
 *       for objects, {@code List<Object>} for arrays, {@code String}, {@code
 *       Long}/{@code Double}, {@code Boolean}, {@code null}). They have never
 *       heard of {@link dev.oasp.client.types.Conversation} or any other
 *       protocol type.
 *   <li>{@link dev.oasp.client.json.JsonCodec} and its default
 *       implementation, {@link dev.oasp.client.json.HandRolledJsonCodec} -
 *       the mapping layer. This is where protocol types are translated to
 *       and from the generic tree the engine produces.
 * </ul>
 *
 * <p>Keeping parsing separate from mapping means the error-prone part
 * (string escaping, recursive-descent parsing) can be unit-tested directly
 * against plain JSON text, with no protocol types involved at all.
 *
 * <p>The field names used when mapping protocol types to/from JSON are the
 * record component names, verbatim, matching oasp-standard's vendored JSON
 * Schemas under {@code src/test/resources/spec-schemas/}.
 *
 * <p>Two discriminated unions drive most of the mapping layer's structure:
 *
 * <ul>
 *   <li>{@code resourceType} - every {@link dev.oasp.client.types.Resource}
 *       carries this FHIR-style discriminator. {@link
 *       dev.oasp.client.json.ResourceDispatch} reads it and routes to the
 *       matching concrete resource, falling back to {@link
 *       dev.oasp.client.types.UnknownResource} for a value this SDK version
 *       doesn't recognise - never an exception.
 *   <li>{@code type} - each {@link dev.oasp.client.types.Event} variant's own
 *       sub-discriminator, one level beneath {@code resourceType:"Event"}.
 *       {@link dev.oasp.client.json.EventReaders} applies the same
 *       recognise-or-fall-back-to-{@link dev.oasp.client.types.UnknownEvent}
 *       treatment. {@link dev.oasp.client.types.SessionResource}'s embedded
 *       {@code type} union gets the identical treatment one level further
 *       down, via {@link dev.oasp.client.json.SessionResourceReaders}.
 * </ul>
 *
 * <p>Every protocol enum (de)serializes through its own {@code wireValue()}/
 * {@code fromWire(String)} pair - never {@code name()}/{@code valueOf} -
 * since several wire values (e.g. {@code "createConversation"}) aren't a
 * simple case transform of the Java constant name.
 */
package dev.oasp.client.json;
