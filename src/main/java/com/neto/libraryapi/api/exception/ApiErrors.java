package com.neto.libraryapi.api.exception;

import com.neto.libraryapi.exception.BusinessException;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiErrors {

    private List<String> errors;

    public ApiErrors(BindingResult bindingResult) {
        this.errors = new ArrayList<>();

        bindingResult.getAllErrors().forEach( error ->
                this.errors.add(error.getDefaultMessage()));
    }

    public ApiErrors(BusinessException exception) {
        this.errors = Arrays.asList(exception.getMessage());
    }

    public List<String> getErrors() {
        return errors;
    }
}
