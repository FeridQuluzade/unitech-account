package az.unitech.development.account.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class RestErrorResponse {

    private String uuid;
    private String code;
    private String message;
    private List<ValidationError> checks;
    private Map<String, Object> properties;

    public RestErrorResponse(String uuid, String code, String message) {
        this(uuid, code, message, Collections.emptyList(), Collections.emptyMap());
    }

    public RestErrorResponse(String uuid, ErrorCode errorCode, String message) {
        this(uuid, errorCode.code(), message, Collections.emptyList(), Collections.emptyMap());
    }

    public RestErrorResponse(String uuid,
                             String code,
                             String message,
                             List<ValidationError> checks) {
        this(uuid, code, message, checks, Collections.emptyMap());
    }

    public RestErrorResponse(String uuid,
                             String code,
                             String message,
                             Map<String, Object> properties) {
        this(uuid, code, message, Collections.emptyList(), properties);
    }

    public RestErrorResponse(String uuid,
                             String code,
                             String message,
                             List<ValidationError> checks,
                             Map<String, Object> properties) {
        this.uuid = uuid;
        this.code = code;
        this.message = message;
        this.checks = checks;
        this.properties = properties;
    }

}