package pokorny.ross.dotsub.mappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.springframework.stereotype.Component;

import org.springframework.dao.DataIntegrityViolationException;

@Provider
@Component
public class DataIntegrityViolationExceptionMapper
        extends RestExceptionMapper<DataIntegrityViolationException> {
    public DataIntegrityViolationExceptionMapper() {
        super(Response.Status.BAD_REQUEST);
    }
}
