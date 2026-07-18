package dev.oasp.client.json;

/** Parses JSON string literals, including escape sequences. */
final class JsonStrings {

    private JsonStrings() {}

    static String parseString(JsonReader reader) {
        reader.expect('"');
        StringBuilder result = new StringBuilder();

        while (true) {
            if (!reader.hasNext()) {
                throw reader.error("unterminated string");
            }
            int charPos = reader.position();
            char c = reader.next();

            if (c == '"') {
                return result.toString();
            }
            if (c == '\\') {
                result.append(parseEscape(reader));
            } else if (c < 0x20) {
                // Raw control characters are not legal inside a JSON string
                // literal - they must be escaped by the producer.
                throw reader.errorAt("unescaped control character in string", charPos);
            } else {
                result.append(c);
            }
        }
    }

    private static char parseEscape(JsonReader reader) {
        if (!reader.hasNext()) {
            throw reader.error("unterminated escape sequence");
        }
        int charPos = reader.position();
        char escaped = reader.next();
        return switch (escaped) {
            case '"' -> '"';
            case '\\' -> '\\';
            case '/' -> '/';
            case 'n' -> '\n';
            case 'r' -> '\r';
            case 't' -> '\t';
            case 'b' -> '\b';
            case 'f' -> '\f';
            case 'u' -> parseUnicodeEscape(reader);
            default -> throw reader.errorAt("invalid escape sequence '\\" + escaped + "'", charPos);
        };
    }

    private static char parseUnicodeEscape(JsonReader reader) {
        int start = reader.position();
        if (!reader.hasRemaining(4)) {
            throw reader.errorAt("truncated \\u escape", start);
        }
        String hex = reader.substring(start, start + 4);
        try {
            char value = (char) Integer.parseInt(hex, 16);
            reader.advance(4);
            return value;
        } catch (NumberFormatException e) {
            throw reader.errorAt("invalid \\u escape '" + hex + "'", start);
        }
    }
}
