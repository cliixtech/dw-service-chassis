package common.rest;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class StatusCodeFilter implements ContainerResponseFilter {

    @Override
    public void filter(
            ContainerRequestContext containerRequestContext,
            ContainerResponseContext containerResponseContext) throws IOException {
        if (containerResponseContext.getStatus() == 200) {
            for (Annotation annotation: containerResponseContext.getEntityAnnotations()) {
                if (annotation instanceof OnSuccess) {
                    containerResponseContext.setStatus(((OnSuccess) annotation).value().getStatusCode());
                    break;
                }
            }
        }
    }
}
