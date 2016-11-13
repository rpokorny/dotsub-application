package pokorny.ross.dotsub;

import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

/**
 * Base class of REST API exception mappers that handles common
 * exception formatting logic
 */
public class RestExceptionMapper<E extends Throwable> implements ExceptionMapper<E> {
    protected static final Logger log = Logger.getLogger(RestExceptionMapper.class);


    private final Response.StatusType responseCode;

    //whether or not to log the exception
    private final boolean logException;

    protected RestExceptionMapper(Response.StatusType responseCode) {
        this.responseCode = responseCode;

        //log exceptions that go back to the client as 5xx responses.
        //In dev mode log all exceptions
        this.logException =  isInternalError(responseCode.getStatusCode());
    }

    public Response toResponse(E exception) {
        doLog(exception);
        return Response.status(responseCode).entity(exception).build();
    }

    /**
     * Log the exception if necessary
     */
    protected void doLog(E exception) {
        if (this.logException) {
            log.error("Exception during REST call", exception);
        }
    }

    protected boolean isInternalError(int responseCode) {
        return (responseCode / 100) == 5;
    }
}
