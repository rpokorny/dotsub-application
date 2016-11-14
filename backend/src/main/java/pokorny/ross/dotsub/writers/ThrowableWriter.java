package pokorny.ross.dotsub.writers;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import java.io.OutputStream;
import java.io.IOException;

import java.lang.reflect.Type;
import java.lang.annotation.Annotation;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * JSON writer for Throwables
 */
@Provider
@Produces("application/json")
public class ThrowableWriter implements MessageBodyWriter<Throwable> {
    @Context
    private ObjectMapper objectMapper;

    public long getSize(
            Throwable t,
            Class<?> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType) {
        return -1;
    }

    public boolean isWriteable(
            Class<?> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType) {
        return Throwable.class.isAssignableFrom(type);
    }

    public void writeTo(
            Throwable t,
            Class<?> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String,Object> httpHeaders,
            OutputStream entityStream) throws JsonProcessingException, IOException {
        Map<String, Object> jsonMap = new HashMap<String, Object>(2);

        Optional<String> optMessage = Optional.ofNullable(t.getMessage());
        if (!optMessage.isPresent()) {
            optMessage = Optional.ofNullable(t.getCause())
                .flatMap(c -> Optional.ofNullable(c.getMessage()));
        }

        String message = optMessage.orElse(t.getClass().getName());

        jsonMap.put("error", true);
        jsonMap.put("message", message);

        byte[] json;

        try {
           json = objectMapper.writeValueAsBytes(jsonMap);
        }
        catch (JsonProcessingException e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }

        entityStream.write(json);
    }
}
