package pokorny.ross.dotsub;

import java.util.NoSuchElementException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class NoSuchElementExceptionMapper extends RestExceptionMapper<NoSuchElementException> {
    public NoSuchElementExceptionMapper() {
        super(Response.Status.NOT_FOUND);
    }
}
