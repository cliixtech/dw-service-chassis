package application;

import javax.ws.rs.core.Response.Status;

import lombok.Getter;

@SuppressWarnings("serial")
public class AppException extends RuntimeException {

    public static enum Code {
        OBJECT_NOT_FOUND(Status.NOT_FOUND, "Object not found"),
        OBJECT_ALREADY_EXISTS(Status.CONFLICT, "Object already exists"),
        UNEXPECTED_ERROR(Status.INTERNAL_SERVER_ERROR, "Unexpected error");

        protected String message;
        protected Status status;

        private Code(Status status, String message) {
            this.status = status;
            this.message = message;
        }

        public Status status() {
            return this.status;
        }
    }

    @Getter
    private Code code;

    public AppException(Code code) {
        this(code.message, code);
    }

    public AppException(String message, Code code) {
        super(message);
        this.code = code;
    }

}
