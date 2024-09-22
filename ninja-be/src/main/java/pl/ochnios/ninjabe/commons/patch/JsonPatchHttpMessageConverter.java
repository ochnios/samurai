package pl.ochnios.ninjabe.commons.patch;

import javax.json.Json;
import javax.json.JsonPatch;
import javax.json.JsonReader;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import pl.ochnios.ninjabe.commons.AppConstants;

@Component
public class JsonPatchHttpMessageConverter extends AbstractHttpMessageConverter<JsonPatch> {

    public JsonPatchHttpMessageConverter() {
        super(MediaType.valueOf(AppConstants.PATCH_MEDIA_TYPE));
    }

    @Override
    @NonNull
    protected JsonPatch readInternal(@NonNull Class<? extends JsonPatch> clazz, @NonNull HttpInputMessage inputMessage)
            throws HttpMessageNotReadableException {

        try (JsonReader reader = Json.createReader(inputMessage.getBody())) {
            return Json.createPatch(reader.readArray());
        } catch (Exception e) {
            throw new HttpMessageNotReadableException(e.getMessage(), inputMessage);
        }
    }

    @Override
    protected void writeInternal(@NonNull JsonPatch jsonPatch, @NonNull HttpOutputMessage outputMessage)
            throws HttpMessageNotWritableException {
        throw new NotImplementedException("The write Json patch is not implemented");
    }

    @Override
    protected boolean supports(@NonNull Class<?> clazz) {
        return JsonPatch.class.isAssignableFrom(clazz);
    }
}
