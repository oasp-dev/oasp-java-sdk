package dev.oasp.client;

/**
 * Builds the (provisional) JSON body for {@code sendMessage}. v1alpha1
 * publishes no schema for the message a {@code send} carries, so this SDK
 * uses the minimal shape {@code {"content": <text>}} until oasp-standard
 * pins the contract. Kept tiny and dependency-free: the one field is a
 * single JSON string, so a full serializer would be overkill here.
 */
final class MessageBodies {

    private MessageBodies() {}

    static String of(String content) {
        var out = new StringBuilder("{\"content\":");
        writeJsonString(content, out);
        return out.append('}').toString();
    }

    /** Emits a JSON string literal, escaping the characters JSON forbids raw in one. */
    private static void writeJsonString(String s, StringBuilder out) {
        out.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                case '\b' -> out.append("\\b");
                case '\f' -> out.append("\\f");
                default -> {
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
                }
            }
        }
        out.append('"');
    }
}
