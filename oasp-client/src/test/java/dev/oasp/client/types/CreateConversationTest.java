package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.List;
import org.junit.jupiter.api.Test;

class CreateConversationTest {

    private static final Principal PRINCIPAL = new Principal("user-1", List.of());

    @Test
    void constructsWithValidArgument() {
        CreateConversation request = new CreateConversation(PRINCIPAL);

        assertThat(request.principal()).isEqualTo(PRINCIPAL);
    }

    @Test
    void rejectsNullPrincipal() {
        assertThatNullPointerException()
                .isThrownBy(() -> new CreateConversation(null))
                .withMessageContaining("principal");
    }

    @Test
    void forPrincipalFactoryReturnsEqualInstanceToCanonicalConstructor() {
        CreateConversation viaFactory = CreateConversation.forPrincipal(PRINCIPAL);
        CreateConversation viaConstructor = new CreateConversation(PRINCIPAL);

        assertThat(viaFactory).isEqualTo(viaConstructor);
    }

    @Test
    void forPrincipalFactoryRejectsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> CreateConversation.forPrincipal(null))
                .withMessageContaining("principal");
    }
}
