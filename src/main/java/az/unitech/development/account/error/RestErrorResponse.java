package az.unitech.development.account.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class RestErrorResponse {

    private String uuid;
    private String code;
    private String message;
    private List<ValidationError> checks;

    public RestErrorResponse(String uuid, String code, String message) {
        this(uuid, code, message, Collections.emptyList());
    }

}