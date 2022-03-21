package az.unitech.development.account.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class CommonErrorHandler extends ResponseEntityExceptionHandler {

    @Resource
    private MessageSource messageSource;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ServiceException.class})
    public RestErrorResponse handleServiceExceptions(ServiceException ex) {
        log.error("Service error, uuid: {}, code: {}, message: {}, {}",
                ex.getErrorUuid(), ex.getErrorCode(), ex.getErrorMessage());
        return new RestErrorResponse(ex.getErrorUuid(),
                ex.getErrorCode(),
                ex.getErrorMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public RestErrorResponse handleInternalServerErrors(Exception ex) {
        String uuid = UUID.randomUUID().toString();
        log.error("Error unexpected internal server error, uuid: {}, message: {}",
                uuid, ex.getMessage());
        return new RestErrorResponse(uuid,
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Internal server error");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConstraintViolationException.class})
    public RestErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        String uuid = UUID.randomUUID().toString();
        log.error("Constraint violation error, uuid: {}, message: {}", uuid, ex.getMessage());
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        List<ValidationError> checks = constraintViolations.stream()
                .map((constraintViolation) ->
                        new ValidationError(ErrorLevel.ERROR,
                                constraintViolation.getPropertyPath().toString(),
                                messageSource.getMessage(constraintViolation.getMessage(),
                                        null,
                                        Locale.ENGLISH)))
                .collect(Collectors.toList());
        return new RestErrorResponse(uuid, HttpStatus.BAD_REQUEST.name(),
                "Invalid argument values",
                checks);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex,
                                                         HttpHeaders headers,
                                                         HttpStatus status,
                                                         WebRequest request) {
        return handleBindingException(ex, ex.getBindingResult(), headers);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        return handleBindingException(ex, ex.getBindingResult(), headers);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        String uuid = UUID.randomUUID().toString();
        log.error("Missing request param error, uuid: {}, message: {}", uuid, ex.getMessage());
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(
                new RestErrorResponse(uuid, badRequest.name(), ex.getMessage()),
                headers,
                badRequest);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex,
                                                               HttpHeaders headers,
                                                               HttpStatus status,
                                                               WebRequest request) {
        String uuid = UUID.randomUUID().toString();
        log.error("Missing path variable error, uuid: {}, message: {}", uuid, ex.getMessage());
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(
                new RestErrorResponse(uuid, badRequest.name(), ex.getMessage()),
                headers,
                badRequest);
    }

    private ResponseEntity<Object> handleBindingException(Throwable ex,
                                                          BindingResult bindingResult,
                                                          HttpHeaders headers) {
        String uuid = UUID.randomUUID().toString();
        log.error("Method argument not valid, uuid: {}, message: {}", uuid, ex.getMessage());
        List<ValidationError> checks = new ArrayList<>();
        checks.addAll(bindingResult.getFieldErrors().stream()
                .map(fieldError -> new ValidationError(ErrorLevel.ERROR,
                        fieldError.getField(),
                        errorMessage(fieldError)))
                .collect(Collectors.toList()));

        checks.addAll(bindingResult.getGlobalErrors().stream()
                .map(globalError -> new ValidationError(ErrorLevel.ERROR,
                        globalError.getObjectName(),
                        errorMessage(globalError)))
                .collect(Collectors.toList()));

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(
                new RestErrorResponse(uuid, badRequest.name(), "Invalid Arguments", checks),
                headers,
                badRequest);
    }

    private String errorMessage(ObjectError objectError) {
        return messageSource.getMessage(
                Objects.requireNonNull(objectError.getDefaultMessage()),
                objectError.getArguments(),
                LocaleContextHolder.getLocale());
    }

}