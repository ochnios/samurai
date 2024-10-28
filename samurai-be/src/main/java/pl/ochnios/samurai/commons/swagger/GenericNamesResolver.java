package pl.ochnios.samurai.commons.swagger;

import com.fasterxml.jackson.databind.JavaType;
import io.swagger.v3.core.jackson.TypeNameResolver;
import io.swagger.v3.core.util.PrimitiveType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.text.WordUtils;

public class GenericNamesResolver extends TypeNameResolver {

    @Override
    protected String nameForGenericType(JavaType type, Set<TypeNameResolver.Options> options) {
        String baseName = this.nameForClass(type, options);
        int count = type.containedTypeCount();

        List<String> genericNames = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            final var arg = type.containedType(i);
            String argName = PrimitiveType.fromType(arg) != null
                    ? this.nameForClass(arg, options)
                    : this.nameForType(arg, options);
            genericNames.add(WordUtils.capitalize(argName));
        }
        String generic = genericNames.stream().collect(Collectors.joining(", ", "<", ">"));
        return baseName + generic;
    }
}
