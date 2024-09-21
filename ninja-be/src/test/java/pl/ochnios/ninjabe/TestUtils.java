package pl.ochnios.ninjabe;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.ochnios.ninjabe.model.dtos.pagination.PageRequestDto;

public class TestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String asJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static MultiValueMap<String, String> asParamsMap(PageRequestDto pageRequest) {
        final var params = new LinkedMultiValueMap<String, String>();
        addIfPresent(params, "page", pageRequest.getPage());
        addIfPresent(params, "size", pageRequest.getSize());
        addIfPresent(params, "sortBy", pageRequest.getSortBy());
        addIfPresent(params, "sortDir", pageRequest.getSortDir());
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
