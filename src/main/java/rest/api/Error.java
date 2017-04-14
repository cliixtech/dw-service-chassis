package rest.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import application.AppException;
import application.AppException.Code;

@EqualsAndHashCode
public class Error {

    @Getter
    private String message;

    @Getter
    private Code code;

    public Error(AppException ex) {
        this(ex.getCode(), ex.getMessage());
    }

    public Error(Code code, String message) {
        this.code = code;
        this.message = message;
    }
}
