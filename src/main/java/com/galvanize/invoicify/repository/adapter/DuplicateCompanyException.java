package com.galvanize.invoicify.repository.adapter;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class DuplicateCompanyException extends RuntimeException {
    public DuplicateCompanyException(String s) {
            super("error caught " + s);
        }
    }


