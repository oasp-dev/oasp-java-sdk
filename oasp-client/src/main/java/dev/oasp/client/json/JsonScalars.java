package dev.oasp.client.json;

/** Leaf scalar parsers: JSON numbers, booleans, and {@code null}. */
final class JsonScalars {

    private JsonScalars() {}

    /**
     * Parses a JSON number. Integers (no {@code .}, {@code e}, or {@code E})
     * become {@code Long}; anything with a fractional part or exponent
     * becomes {@code Double} - the split JSON itself doesn't make but Java
     * requires, matching the types {@link JsonWriter} accepts back.
     */
    static Object parseNumber(JsonReader reader) {
        int start = reader.position();
        boolean isFloatingPoint = false;

        if (reader.hasNext() && reader.peek() == '-') {
            reader.next();
        }
        while (reader.hasNext() && Character.isDigit(reader.peek())) {
            reader.next();
        }
        if (reader.hasNext() && reader.peek() == '.') {
            isFloatingPoint = true;
            reader.next();
            while (reader.hasNext() && Character.isDigit(reader.peek())) {
                reader.next();
            }
        }
        if (reader.hasNext() && (reader.peek() == 'e' || reader.peek() == 'E')) {
            isFloatingPoint = true;
            reader.next();
            if (reader.hasNext() && (reader.peek() == '+' || reader.peek() == '-')) {
                reader.next();
            }
            while (reader.hasNext() && Character.isDigit(reader.peek())) {
                reader.next();
            }
        }

        String text = reader.sliceFrom(start);
        if (text.isEmpty() || "-".equals(text)) {
            throw reader.errorAt("invalid number", start);
        }
        try {
            return isFloatingPoint ? (Object) Double.parseDouble(text) : (Object) Long.parseLong(text);
        } catch (NumberFormatException e) {
            throw reader.errorAt("invalid number '" + text + "'", start);
        }
    }

    static Boolean parseBoolean(JsonReader reader) {
        if (reader.tryConsume("true")) {
            return Boolean.TRUE;
        }
        if (reader.tryConsume("false")) {
            return Boolean.FALSE;
        }
        throw reader.error("invalid literal (expected 'true' or 'false')");
    }

    static Object parseNull(JsonReader reader) {
        if (reader.tryConsume("null")) {
            return null;
        }
        throw reader.error("invalid literal (expected 'null')");
    }
}
