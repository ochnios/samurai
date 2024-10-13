package pl.ochnios.ninjabe;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class TestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String asJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static MultiValueMap<String, String> asParamsMap(Object dto) {
        final var params = new LinkedMultiValueMap<String, String>();
        final var fields = dto.getClass().getDeclaredFields();
        for (var field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(dto);
                if (value != null) {
                    if (value instanceof Iterable<?> iterableValue) {
                        for (var param : iterableValue) {
                            params.add(field.getName(), param.toString());
                        }
                    } else {
                        params.add(field.getName(), value.toString());
                    }
                }
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
        return params;
    }

    public static String generateTooLongString(int length) {
        final var sb = new StringBuilder();
        do {
            sb.append('X');
        } while (sb.length() < length);
        return sb.toString();
    }

    private static void addIfPresent(MultiValueMap<String, String> map, String key, Object value) {
        if (value != null) {
            map.add(key, urlEncode(value));
        }
    }

    private static String urlEncode(Object o) {
        return URLEncoder.encode(o.toString(), StandardCharsets.UTF_8);
    }
}
