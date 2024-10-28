package pl.ochnios.samurai.commons.patch;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JsonPatchDto {

    private final String op;
    private final String path;
    private final Object value;
}
