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
 * <p>The field names used when mapping protocol types to/from JSON (e.g.
 * {@code subject}, {@code createdAt}, {@code type}) are the record component
 * names, verbatim. These are ASSUMED v0 names pending issue #2, which will
 * verify them against the actual OASP protocol specification.
 */
package dev.oasp.client.json;
