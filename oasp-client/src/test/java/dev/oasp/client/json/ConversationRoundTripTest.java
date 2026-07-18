package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.types.AgentVersionRef;
import dev.oasp.client.types.Conversation;
import dev.oasp.client.types.PrincipalKind;
import dev.oasp.client.types.PrincipalRef;
import dev.oasp.client.types.Scope;
import dev.oasp.client.types.ScopeLevel;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConversationRoundTripTest {

    private final HandRolledJsonCodec codec = new HandRolledJsonCodec();

    @Test
    void roundTripsWithPreviousSessionIds() {
        Conversation conversation = new Conversation(
                "conv-1",
                new Scope(ScopeLevel.WORKSPACE, "ws-1"),
                new PrincipalRef(PrincipalKind.USER, "user-1"),
                "session-2",
                new AgentVersionRef("agent-1", 3L),
                List.of("session-0", "session-1"));

        Conversation result = codec.read(codec.write(conversation), Conversation.class);

        assertThat(result).isEqualTo(conversation);
    }

    @Test
    void roundTripsWithNoPreviousSessions() {
        Conversation conversation = new Conversation(
                "conv-2",
                new Scope(ScopeLevel.TENANT, "tenant-1"),
                new PrincipalRef(PrincipalKind.SERVICE, "service-1"),
                "session-1",
                new AgentVersionRef("agent-1", 1L),
                List.of());

        Conversation result = codec.read(codec.write(conversation), Conversation.class);

        assertThat(result).isEqualTo(conversation);
    }
}
