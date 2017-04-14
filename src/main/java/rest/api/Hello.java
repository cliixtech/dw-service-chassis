package rest.api;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
public class Hello {

    @NotNull
    @NotEmpty
    private String name;
    @UnwrapValidatedValue
    @Length(min = 3, max = 3)
    @JsonProperty("location_code")
    private Optional<String> locationCode;
}
