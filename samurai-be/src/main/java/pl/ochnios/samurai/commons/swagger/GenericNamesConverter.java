package pl.ochnios.samurai.commons.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;

public class GenericNamesConverter extends ModelResolver {

    public GenericNamesConverter(ObjectMapper mapper) {
        super(mapper, new GenericNamesResolver());
    }
}
