package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JsonCodecTest {

    @Test
    void getDefaultReturnsTheHandRolledCodecWhenNoProviderIsRegistered() {
        // This test module registers no dev.oasp.client.json.JsonCodec
        // service provider (there's no META-INF/services entry for it), so
        // the ServiceLoader lookup in JsonCodec.getDefault() must find
        // nothing and fall back to HandRolledJsonCodec, exactly as
        // documented on that method.
        JsonCodec codec = JsonCodec.getDefault();

        assertThat(codec).isInstanceOf(HandRolledJsonCodec.class);
    }
}
