package com.tl.spring_boot1.util;

import com.tl.spring_boot1.exception.InvalidUserDataException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserQueryParamValidatorTest {
    UserQueryParamValidator userQueryParamValidator = new UserQueryParamValidator();

    // Testing validateSort()
    @ParameterizedTest
    @ValueSource(strings = { "id", "name", "email", "city", "age", "role"})
    public void shouldAllowColumnsToSort(String column){
        assertDoesNotThrow(() -> {
            userQueryParamValidator.validateSort(column);
        });
    }

    @Test
    public void shouldNotAllowInvalidColumnsToSort(){
        assertThrows(InvalidUserDataException.class, () -> {
            userQueryParamValidator.validateSort("password");
        });
    }

    // Testing validateOrder()
    @ParameterizedTest
    @ValueSource(strings = {"asc", "desc"})
    public void shouldAllowAscToOrder(String order){
        assertDoesNotThrow(() -> {
            userQueryParamValidator.validateOrder(order);
        });
    }

    @Test
    public void shouldNotAllowInvalidColumnsToOrder(){
        assertThrows(InvalidUserDataException.class, () -> {
            userQueryParamValidator.validateOrder("up");
        });
    }

    // Testing validateCrossFieldAgeRange()
    @Test
    public void shouldPassWhenMinAgeIsLessThanMaxAge(){
        assertDoesNotThrow(() -> {
            userQueryParamValidator.validateCrossFieldAgeRange(10, 20);
        });
    }

    @Test
    public void shouldPassWhenMinAgeIsEqualToMaxAge(){
        assertDoesNotThrow(() -> {
            userQueryParamValidator.validateCrossFieldAgeRange(10, 10);
        });
    }

    @Test
    public void shouldPassWhenMinAgeIsNull(){
        assertDoesNotThrow(() -> {
            userQueryParamValidator.validateCrossFieldAgeRange(null, 20);
        });
    }

    @Test
    public void shouldPassWhenMaxAgeIsNull(){
        assertDoesNotThrow(() -> {
            userQueryParamValidator.validateCrossFieldAgeRange(10, null);
        });
    }

    @Test
    public void shouldPassWhenMinAgeAndMaxAgeIsNull(){
        assertDoesNotThrow(() -> {
            userQueryParamValidator.validateCrossFieldAgeRange(null, null);
        });
    }

    @Test
    public void shouldNotPassWhenMinAgeIsGreaterThenMaxAge(){
        assertThrows(InvalidUserDataException.class, () -> {
            userQueryParamValidator.validateCrossFieldAgeRange(10, 5);
        });
    }

}
