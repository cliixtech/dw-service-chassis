package rest.resources;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import rest.api.Hello;
import application.AppException;
import application.AppException.Code;

import common.rest.OnSuccess;
import io.swagger.annotations.Api;

@Api("/hello")
@Path("/hello")
@Produces(MediaType.APPLICATION_JSON)
public class HelloResource {

    private Map<String, Hello> hellos = new HashMap<>();

    @GET
    @Path("{id}")
    @OnSuccess(Status.OK)
    public Optional<Hello> get(@PathParam("id") String id) {
        return Optional.ofNullable(this.hellos.get(id));
    }

    @POST
    @Path("{id}")
    @OnSuccess(Status.CREATED)
    public Hello create(@PathParam("id") String id, @Valid Hello hello) {
        if (this.hellos.containsKey(id)) {
            throw new AppException(Code.OBJECT_ALREADY_EXISTS);
        }

        this.hellos.put(id, hello);
        return hello;
    }

    @DELETE
    @Path("{id}")
    @OnSuccess(Status.NO_CONTENT)
    public void revoke(@PathParam("id") String id) {
        this.hellos.remove(id);
    }
}
