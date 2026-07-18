package dev.oasp.client.types;

/**
 * The FHIR-style base of every OASP <em>resource</em>: a value that is
 * independently addressable and always carries a {@code resourceType}
 * discriminator naming its own type, verbatim, in PascalCase (e.g.
 * {@code "Conversation"}, {@code "Session"}).
 *
 * <p>This is distinct from an embedded <em>datatype</em> - e.g. {@link Scope}
 * or {@link PrincipalRef} - which only ever appears nested inside a resource
 * and is dispatched by the field name that embeds it, never by a
 * discriminator of its own. See {@code docs/spec/resources.md} in
 * oasp-standard for the normative resource/datatype split this interface
 * mirrors.
 *
 * <p>{@code sealed}, per the same technique used throughout this package
 * (see {@link Event}): {@code permits} below is a closed, compiler-checked
 * list of every resource type this SDK version knows how to represent. The
 * risk that closes off - what happens when a newer server returns a
 * resource type this SDK predates - is exactly what {@link UnknownResource}
 * is for for: a permitted catch-all that preserves the raw payload instead of
 * failing to deserialize it at all. (Routing an unrecognised
 * {@code resourceType} to {@code UnknownResource} is the job of the JSON
 * deserialization layer, not this interface.)
 *
 * @see UnknownResource
 */
public sealed interface Resource
        permits Principal, Conversation, Session, AuditEvent, Event, UnknownResource {

    /**
     * This resource's PascalCase type name, e.g. {@code "Conversation"}.
     */
    String resourceType();
}
