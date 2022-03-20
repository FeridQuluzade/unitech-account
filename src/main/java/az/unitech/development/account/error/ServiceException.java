package az.unitech.development.account.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceException extends RuntimeException {

    private final String errorUuid;
    private final String errorCode;
    private final String errorMessage;
    private final Map<String, Object> properties = new HashMap<>();

    public ServiceException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorUuid = UUID.randomUUID().toString();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static ServiceException of(ErrorCode errorCode, String errorMessage) {
        return new ServiceException(errorCode.code(), errorMessage);
    }

    @Override
    public String getMessage() {
        return this.errorMessage;
    }

}