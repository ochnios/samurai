package pl.ochnios.ninjabe;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String asJsonString(final Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateTooLongString(int length) {
        final var sb = new StringBuilder();
        do {
            sb.append('X');
        } while (sb.length() < length);
        return sb.toString();
    }
}
