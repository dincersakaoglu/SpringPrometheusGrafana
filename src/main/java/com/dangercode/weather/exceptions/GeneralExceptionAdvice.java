package com.dangercode.weather.exceptions;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GeneralExceptionAdvice extends ResponseEntityExceptionHandler {
    //Alttaki controllerda  @NotBlank exception atarsa bu yakalar.
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NotNull HttpHeaders headers,
                                                                  @NotNull HttpStatusCode status,
                                                                  @NotNull WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        logger.info(String.format("Api validation error: %s", errors));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    //Controllerda @CityNameConstraint exception atarsa bu yakalar.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handle(ConstraintViolationException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.BAD_REQUEST);
    }


    /**
     * Exception handler for handling the 'RequestNotPermitted' exception, which is thrown in case of exceeding the rate limit.
     * Returns a ResponseEntity with a message indicating that the rate limit has been exceeded and advising the user to try the request again later.
     *
     * @param exception The instance of the 'RequestNotPermitted' exception.
     * @return ResponseEntity containing an informative message and 'TOO_MANY_REQUESTS' HTTP status.
     */
    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<String> handle(RequestNotPermitted exception) {
        return new ResponseEntity<>("Rate limit exceeded. Please try your request again later!", HttpStatus.TOO_MANY_REQUESTS);
    }
}
