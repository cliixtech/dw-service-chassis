package application;

import javax.validation.ValidationException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rest.api.Error;
import application.AppException.Code;
import io.dropwizard.jersey.optional.EmptyOptionalException;

@Provider
public class AppExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = LoggerFactory.getLogger(AppExceptionMapper.class);

    @Override
    public Response toResponse(Exception ex) {
        Status status;
        Error error = null;
        // Dropwizard/Jersey exceptions first
        if (ex instanceof EmptyOptionalException) {
            status = Status.NOT_FOUND;
            error = new Error(Code.OBJECT_NOT_FOUND, ex.getMessage());
        } else if (ex instanceof NotFoundException) {
            status = Status.NOT_FOUND;
            error = new Error(Code.OBJECT_NOT_FOUND, ex.getMessage());
        } else if (ex instanceof ValidationException) {
            LOG.warn("Invalid input: {}", ex.getMessage());
            status = Status.BAD_REQUEST;
            error = new Error(Code.UNEXPECTED_ERROR, ex.getMessage());
        } else if (ex instanceof AppException) {
            // Application exception now
            Code code = ((AppException) ex).getCode();
            status = code.status();
            error = new Error(code, ex.getMessage());
        } else {
            LOG.error("Internal Server Error", ex);
            status = Status.INTERNAL_SERVER_ERROR;
            error = new Error(Code.UNEXPECTED_ERROR, ex.getMessage());
        }
        return Response.status(status).entity(error).build();
    }
}
