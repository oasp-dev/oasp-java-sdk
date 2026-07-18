package dev.oasp.client.json;

/** Writes JSON string literals, including the surrounding quotes and escaping. */
final class JsonStringEscaper {

    private JsonStringEscaper() {}

    /**
     * Escaped: {@code "} and {@code \} (structurally required), plus the
     * named two-character escapes JSON defines for common control characters
     * ({@code \n \r \t \b \f}). Any other control character ({@code
     * U+0000}-{@code U+001F}) falls back to a generic four-hex-digit unicode
     * escape, since JSON forbids raw control characters in a string literal.
     * {@code /} is deliberately left unescaped: {@code \/} is JSON-legal but
     * optional, and unescaped {@code /} is both valid and more readable.
     */
    static void writeString(String s, StringBuilder out) {
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
