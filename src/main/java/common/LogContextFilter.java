package common;

import java.io.IOException;
import java.util.UUID;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LogContextFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LogContextFilter.class);
    private static final String CONTEXT = "ctx";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String context = UUID.randomUUID().toString();
        MDC.put(CONTEXT, context);
        this.logRequest(requestContext);
    }

    private void logRequest(ContainerRequestContext req) {
        LOG.info("{} {} \"{}\"", req.getMethod(), req.getUriInfo().getRequestUri(), req.getHeaderString("User-Agent"));

    }

}
