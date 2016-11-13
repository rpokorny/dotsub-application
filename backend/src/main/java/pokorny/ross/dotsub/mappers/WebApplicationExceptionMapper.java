package pokorny.ross.dotsub;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.WebApplicationException;

@Provider
public class WebApplicationExceptionMapper extends RestExceptionMapper<WebApplicationException> {
    public WebApplicationExceptionMapper () {
        //NOTE This argument is ignored in favor of the
        //response code in the exception
        super(Response.Status.INTERNAL_SERVER_ERROR);
    }

    @Override
    public Response toResponse(WebApplicationException e) {
        Response exceptionResponse = e.getResponse();

        //log if a 5xx error
        if (isInternalError(exceptionResponse.getStatus())) {
            log.error("5xx-level WebApplicationException", e);
        }

        return Response.status(exceptionResponse.getStatus()).entity(e).build();
    }
}
