package uk.co.huntersix.spring.rest.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

@ResponseStatus(CONFLICT)
public class PersonAlreadyExistException extends RuntimeException {

    public PersonAlreadyExistException(String message) {
        super(message);
    }

    public PersonAlreadyExistException() {
        super();
    }
}