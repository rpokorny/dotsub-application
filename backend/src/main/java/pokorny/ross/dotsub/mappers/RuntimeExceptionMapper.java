package pokorny.ross.dotsub.mappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class RuntimeExceptionMapper extends RestExceptionMapper<RuntimeException> {
    public RuntimeExceptionMapper() {
        super(Response.Status.INTERNAL_SERVER_ERROR);
    }
}
