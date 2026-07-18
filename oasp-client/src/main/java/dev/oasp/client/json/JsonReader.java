package dev.oasp.client.json;

/**
 * The character cursor shared by {@link JsonParser} and its helpers
 * ({@link JsonStructures}, {@link JsonStrings}, {@link JsonScalars}):
 * position tracking, lookahead, and the {@link JsonException} factories that
 * report a message plus the offending position/character.
 */
final class JsonReader {

    private final String json;
    private int pos;

    JsonReader(String json) {
        this.json = json;
    }

    boolean hasNext() {
        return pos < json.length();
    }

    char peek() {
        if (!hasNext()) {
            throw error("unexpected end of input");
        }
        return json.charAt(pos);
    }

    /** Consumes and returns the current character. */
    char next() {
        char c = peek();
        pos++;
        return c;
    }

    void skipWhitespace() {
        while (hasNext() && Character.isWhitespace(json.charAt(pos))) {
            pos++;
        }
    }

    void expect(char c) {
        if (!hasNext() || json.charAt(pos) != c) {
            throw error("expected '" + c + "'");
        }
        pos++;
    }

    /** If {@code literal} matches at the cursor, consumes it and returns true. */
    boolean tryConsume(String literal) {
        if (json.startsWith(literal, pos)) {
            pos += literal.length();
            return true;
        }
        return false;
    }

    boolean hasRemaining(int n) {
        return pos + n <= json.length();
    }

    String substring(int start, int end) {
        return json.substring(start, end);
    }

    void advance(int n) {
        pos += n;
    }

    String sliceFrom(int start) {
        return json.substring(start, pos);
    }

    int position() {
        return pos;
    }

    JsonException error(String message) {
        return errorAt(message, pos);
    }

    JsonException errorAt(String message, int position) {
        String context =
                (position < json.length()) ? "'" + json.charAt(position) + "'" : "<end of input>";
        return new JsonException(message + " at position " + position + " (" + context + ")");
    }
}
