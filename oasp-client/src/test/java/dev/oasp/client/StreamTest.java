package dev.oasp.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.types.AssistantMessageStartEvent;
import dev.oasp.client.types.AssistantMessageTextEvent;
import dev.oasp.client.types.Event;
import dev.oasp.client.types.SessionRunStatus;
import dev.oasp.client.types.StatusEvent;
import dev.oasp.client.types.UnknownEvent;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class StreamTest extends WireMockTestBase {

    @Test
    void parsesSseFramesIntoEventsIncludingUnknownType() {
        stubFor(get(urlEqualTo("/sessions/session-1/events"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/event-stream")
                        .withBody(sseBody("session-events.sse"))));

        List<Event> events;
        try (Stream<Event> stream = client.sessions().stream("session-1")) {
            events = stream.toList();
        }

        assertThat(events).hasSize(4);
        assertThat(events.get(0)).isInstanceOfSatisfying(AssistantMessageStartEvent.class, e -> {
            assertThat(e.id()).isEqualTo("evt-0001");
            assertThat(e.messageId()).isEqualTo("msg-1");
        });
        assertThat(events.get(1)).isInstanceOfSatisfying(AssistantMessageTextEvent.class, e -> assertThat(e.delta())
                .isEqualTo("Hello, weaver."));
        assertThat(events.get(2)).isInstanceOfSatisfying(UnknownEvent.class, e -> {
            assertThat(e.id()).isEqualTo("evt-0003");
            assertThat(e.type()).isEqualTo("quantum_flux");
        });
        assertThat(events.get(3)).isInstanceOfSatisfying(StatusEvent.class, e -> assertThat(e.status())
                .isEqualTo(SessionRunStatus.IDLE));
    }
}
