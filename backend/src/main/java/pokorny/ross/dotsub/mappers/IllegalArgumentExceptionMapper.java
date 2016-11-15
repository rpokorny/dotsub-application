package pokorny.ross.dotsub.mappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper extends RestExceptionMapper<IllegalArgumentException> {
    public IllegalArgumentExceptionMapper() {
        super(Response.Status.BAD_REQUEST);
    }
}
