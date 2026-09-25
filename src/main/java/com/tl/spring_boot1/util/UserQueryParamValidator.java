package com.tl.spring_boot1.util;

import com.tl.spring_boot1.exception.InvalidUserDataException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

//page, limit, sort, order, name, email, city, minAge, maxAge
@Component
public class UserQueryParamValidator {

    // white listing allowed sort columns
    private final List<String> ALLOWED_SORT_COLUMNS = Arrays.asList("id", "name", "email", "city", "age", "role");
    private final List<String> ALLOWED_ORDER_VALUES = Arrays.asList("asc", "desc");

    public void validateSort( String sort ){
        if(!ALLOWED_SORT_COLUMNS.contains(sort)){
            throw new InvalidUserDataException("Invalid sort column: " + sort + ". Allowed values are: " + ALLOWED_SORT_COLUMNS);
        }
    }

    public void validateOrder( String order ){
        if(!ALLOWED_ORDER_VALUES.contains(order)){
            throw new InvalidUserDataException("Invalid order value: " + order + ". Allowed values are: " + ALLOWED_ORDER_VALUES);
        }
    }

    public void validateCrossFieldAgeRange( Integer minAge, Integer maxAge ){
        if( minAge!=null && maxAge!=null && maxAge < minAge ){
            throw new InvalidUserDataException("Invalid age range: maxAge cannot be less than minAge.");
        }
    }


}
