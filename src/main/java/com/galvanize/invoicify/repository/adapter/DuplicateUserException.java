package com.galvanize.invoicify.repository.adapter;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <h2>
 *     This class defines the custom exception DuplicateCompanyException, which inherits from RuntimeException. It addresses
 *      the issue of computing logic against duplication of keys. Since the User table must contain non-null, unique
 *      String name entries, this exception prevents the user from assigning a name to a User that already exists
 *      in the table. This also handles redirecting the user in these instances and prompting to adjust
 *      serialization so table integrity in tact and aligned with the rest of the system
 * </h2>
 */

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException(String s) {
        super("error caught " + s);
    }
}
