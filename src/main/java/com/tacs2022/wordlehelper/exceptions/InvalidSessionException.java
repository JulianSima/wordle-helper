package com.tacs2022.wordlehelper.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvalidSessionException extends RuntimeException {
    public InvalidSessionException(){
        super("Invalid session");
    }
}
