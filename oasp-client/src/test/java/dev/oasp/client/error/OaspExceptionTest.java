package dev.oasp.client.error;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class OaspExceptionTest {

    @Test
    void isUnchecked() {
        // Proves OaspException requires no checked-exception ceremony from
        // callers: it's a RuntimeException, not a checked Exception.
        assertThat(RuntimeException.class.isAssignableFrom(OaspException.class)).isTrue();
    }

    @Test
    void permittedSubclassesAreExactlyTheClosedSet() {
        // Reflective proof (mirroring AuditEventTest) that the taxonomy of
        // OASP failures is really closed to these five types, not just
        // closed "by convention" - getPermittedSubclasses() only returns
        // anything because OaspException is declared sealed.
        assertThat(OaspException.class.getPermittedSubclasses())
                .containsExactlyInAnyOrder(
                        OaspTransportException.class,
                        OaspAuthException.class,
                        OaspNotFoundException.class,
                        OaspConflictException.class,
                        OaspProtocolException.class);
    }

    @Test
    void transportExceptionPreservesCause() {
        IOException cause = new IOException("connection reset");

        OaspTransportException exception = new OaspTransportException("request failed", cause);

        assertThat(exception.getCause()).isSameAs(cause);
        assertThat(exception.getMessage()).isEqualTo("request failed");
    }
}
