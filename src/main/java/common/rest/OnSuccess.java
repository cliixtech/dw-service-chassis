package common.rest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.ws.rs.NameBinding;
import javax.ws.rs.core.Response.Status;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface OnSuccess {
    Status value();

}