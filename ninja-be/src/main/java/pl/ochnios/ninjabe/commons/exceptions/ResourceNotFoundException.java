package pl.ochnios.ninjabe.commons.exceptions;

public class ResourceNotFoundException extends ApplicationException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(Class<?> resourceClass, Object key) {
        String resourceName = resourceClass.getSimpleName();
        String message =
                String.format("Not found %s resource with key=%s", resourceName, key.toString());
        return new ResourceNotFoundException(message);
    }
}
