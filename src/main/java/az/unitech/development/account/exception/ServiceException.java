package az.unitech.development.account.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceException extends RuntimeException {

    private String errorUuid;
    private String errorCode;
    private String errorMessage;
    private Map<String, Object> properties = new HashMap<>();
    private IOException ioException;

    public ServiceException(String errorUuid, String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorUuid = errorUuid;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ServiceException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorUuid = UUID.randomUUID().toString();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ServiceException(String errorCode, String errorMessage, IOException ioException) {
        super(errorMessage);
        this.errorUuid = UUID.randomUUID().toString();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.ioException = ioException;
    }

    public static ServiceException of(ErrorCode errorCode, String errorMessage) {
        return new ServiceException(errorCode.code(), errorMessage);
    }

    public static ServiceException of(ErrorCode errorCode, String errorMessage, IOException e) {
        return new ServiceException(errorCode.code(), errorMessage, e);
    }

    public ServiceException set(String name, Object value) {
        properties.put(name, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T) properties.get(name);
    }

    public boolean is(ErrorCode errorCode) {
        return errorCode != null && errorCode.code().equals(this.errorCode);
    }

    public String formatProperties() {
        return Optional.ofNullable(properties)
                .map(Map::keySet)
                .orElse(Collections.emptySet())
                .stream()
                .map(this::formatProperty)
                .collect(Collectors.joining(", "));
    }

    private String formatProperty(String key) {
        return key + ": " + properties.get(key);
    }

    @Override
    public String getMessage() {
        return this.errorMessage;
    }
}