package com.tl.spring_boot1.service;

import com.tl.spring_boot1.dto.users.UserCreateRequest;
import com.tl.spring_boot1.dto.users.UserPatchRequest;
import com.tl.spring_boot1.dto.users.UserResponse;
import com.tl.spring_boot1.exception.InvalidUserDataException;
import com.tl.spring_boot1.exception.UserNotFoundException;
import com.tl.spring_boot1.model.User;
import com.tl.spring_boot1.repository.UserRepository;
import com.tl.spring_boot1.support.UserMother;
import com.tl.spring_boot1.support.UserPatchRequestBuilder;
import com.tl.spring_boot1.util.UserQueryParamValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserQueryParamValidator userQueryParamValidator;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Captor
    ArgumentCaptor<User> argumentCaptor;

    // Testing createUser
    @Nested
    class CreateUser{

        // since JUnit runs each Test method with new instance so this will be freshly created for each method as well.
        UserCreateRequest request = getTestUserCreateRequest();

        @BeforeEach
        void setUp(){
            when(passwordEncoder.encode(request.getPassword())).thenReturn("HASHED");
            // returned value of save() is going to be used at many places, so mock properly
            when(userRepository.save(any(User.class))).thenAnswer( invocation -> {
                User userToBeSaved = invocation.getArgument(0);
                userToBeSaved.setId(1);
                return userToBeSaved;
            });
        }

        @Test
        void createUserShouldPassCorrectValuesFromRequest(){
            userService.createUser(request);

            verify(userRepository).save(argumentCaptor.capture());
            User userToBeSaved = argumentCaptor.getValue();

            assertEquals(request.getName(), userToBeSaved.getName());
            assertEquals(request.getEmail(), userToBeSaved.getEmail());
            assertEquals(request.getAge(), userToBeSaved.getAge());
            assertEquals(request.getCity(), userToBeSaved.getCity());
            assertEquals("HASHED", userToBeSaved.getPassword());
            assertEquals("USER", userToBeSaved.getRole());
        }

        // important and explicit
        @Test
        void createUserShouldPassHashedPassword(){
            userService.createUser(request);

            verify(userRepository).save(argumentCaptor.capture());
            User userToBeSaved = argumentCaptor.getValue();

            assertEquals("HASHED", userToBeSaved.getPassword());
        }

        // important and explicit
        @Test
        void createUserShouldPassDefaultUser(){
            userService.createUser(request);

            verify(userRepository).save(argumentCaptor.capture());
            User userToBeSaved = argumentCaptor.getValue();

            assertEquals("USER", userToBeSaved.getRole());
        }

        @Test
        void createUserShouldPassCorrectValuesInUserResponseDTOFromRequest(){
            UserResponse response = userService.createUser(request);

            assertEquals(1, response.getId());
            assertEquals("USER", response.getRole());
            assertEquals(request.getName(), response.getName());
            assertEquals(request.getEmail(), response.getEmail());
            assertEquals(request.getAge(), response.getAge());
            assertEquals(request.getCity(), response.getCity());
        }
    }


    // Testing findUserById()

    @Test
    void findUserByIdShouldReturnCorrectUserResponseWhenUserExist(){
        User testUser = UserMother.aUser(); // this test user id is also 1
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        UserResponse response = userService.findUserById(1);

        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getName(), response.getName());
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals(testUser.getAge(), response.getAge());
        assertEquals(testUser.getCity(), response.getCity());
        assertEquals(testUser.getRole(), response.getRole());
    }

    @Test
    void findUserByIdShouldThrowExceptionWhenUserWithIdDoesNotExist(){
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.findUserById(1);
        });
    }

    // Testing patchUser

    @Test
    void patchUserShouldReturnCorrectUserResponseAfterSuccessfullPatch(){
        UserPatchRequest request = UserPatchRequestBuilder.aUserPatchRequestBuilder().build();
        User user = UserMother.aUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserResponse response = userService.patchUser(user.getId(), request);

        assertEquals(user.getId(), response.getId());// should be same and not updatable
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getAge(), response.getAge());
        assertEquals(request.getCity(), response.getCity());
    }

    @Test
    void patchUserShouldThrowExceptionWhenUserWithIdDoesNotExist(){
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        UserPatchRequest request = UserPatchRequestBuilder.aUserPatchRequestBuilder().build();

        assertThrows(UserNotFoundException.class, () -> {
            userService.patchUser(1, request);
        });
    }

    @Test
    void patchUserShouldThrowExceptionWhenNameIsEmpty(){ //""
        UserPatchRequest request = UserPatchRequestBuilder.aUserPatchRequestBuilder().withName("").build();
        User user = UserMother.aUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(InvalidUserDataException.class, () -> {
            userService.patchUser(user.getId(), request);
        });
    }

    @Test
    void patchUserShouldThrowExceptionWhenNameIsBlank(){ //" "
        UserPatchRequest request = UserPatchRequestBuilder.aUserPatchRequestBuilder().withName(" ").build();
        User user = UserMother.aUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(InvalidUserDataException.class, () -> {
            userService.patchUser(user.getId(), request);
        });
    }

    @Test
    void patchUserShouldThrowExceptionWhenEmailIsEmpty(){ //""
        UserPatchRequest request = UserPatchRequestBuilder.aUserPatchRequestBuilder().withEmail("").build();
        User user = UserMother.aUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(InvalidUserDataException.class, () -> {
            userService.patchUser(user.getId(), request);
        });
    }

    @Test
    void patchUserShouldThrowExceptionWhenEmailIsBlank(){ //" "
        UserPatchRequest request = UserPatchRequestBuilder.aUserPatchRequestBuilder().withEmail(" ").build();
        User user = UserMother.aUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(InvalidUserDataException.class, () -> {
            userService.patchUser(user.getId(), request);
        });
    }

    @Test
    void patchUserShouldNotUpdateWhenRequestFieldsAreNull(){
        UserPatchRequest request = UserPatchRequestBuilder.aUserPatchRequestBuilder().withName(null).withAge(null).withEmail(null).withCity(null).build();
        User user = UserMother.aUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserResponse response = userService.patchUser(user.getId(), request);

        assertEquals(user.getName(), response.getName());
        assertEquals(user.getAge(),  response.getAge());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getCity(), response.getCity());
    }

    @Test
    void patchUserShouldAllowPartialUpdateNameOnly(){
        UserPatchRequest request = UserPatchRequestBuilder.aUserPatchRequestBuilder().withName("patched name").withAge(null).withEmail(null).withCity(null).build();
        User user = UserMother.aUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserResponse response = userService.patchUser(user.getId(), request);

        assertEquals(request.getName(), response.getName());
        assertEquals(user.getAge(), response.getAge());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getCity(), response.getCity());
    }

    @Test
    void patchUserShouldAllowPartialUpdateAgeOnly(){
        UserPatchRequest request = UserPatchRequestBuilder.aUserPatchRequestBuilder().withName(null).withAge(56).withEmail(null).withCity(null).build();
        User user = UserMother.aUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserResponse response = userService.patchUser(user.getId(), request);

        assertEquals(user.getName(), response.getName());
        assertEquals(request.getAge(), response.getAge());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getCity(), response.getCity());
    }

    @Test
    void patchUserShouldAllowPartialUpdateEmailOnly(){
        UserPatchRequest request = UserPatchRequestBuilder.aUserPatchRequestBuilder().withName(null).withAge(null).withEmail("patched@test.com").withCity(null).build();
        User user = UserMother.aUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserResponse response = userService.patchUser(user.getId(), request);

        assertEquals(user.getName(), response.getName());
        assertEquals(user.getAge(), response.getAge());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(user.getCity(), response.getCity());
    }

    @Test
    void patchUserShouldAllowPartialUpdateCityOnly(){
        UserPatchRequest request = UserPatchRequestBuilder.aUserPatchRequestBuilder().withName(null).withAge(null).withEmail(null).withCity("patched city").build();
        User user = UserMother.aUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserResponse response = userService.patchUser(user.getId(), request);

        assertEquals(user.getName(), response.getName());
        assertEquals(user.getAge(), response.getAge());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(request.getCity(), response.getCity());
    }

    @Test
    void patchUserShouldAllowBlankCityUpdate(){// 'city' field is non-mandatory
        UserPatchRequest request = UserPatchRequestBuilder.aUserPatchRequestBuilder().withCity("  ").build();
        User user = UserMother.aUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserResponse response = userService.patchUser(user.getId(), request);

        assertEquals(request.getCity(), response.getCity());
    }

    @Test
    void patchUserShouldAllowEmptyCityUpdate(){ // 'city' field is non-mandatory
        UserPatchRequest request = UserPatchRequestBuilder.aUserPatchRequestBuilder().withCity("").build();
        User user = UserMother.aUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserResponse response = userService.patchUser(user.getId(), request);

        assertEquals(request.getCity(), response.getCity());
    }

    // Testing deleteUser

    @Test
    void deleteUserShouldDeleteUserSuccessfully(){
        User user = UserMother.aUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> {
            userService.deleteUser(user.getId());
        });
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUserShouldBeIdempotent(){
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> {
            userService.deleteUser(1);
        });
        verify(userRepository, never()).delete(any());
    }


    // helper methods
    private UserCreateRequest getTestUserCreateRequest(){
        UserCreateRequest request = new UserCreateRequest();
        request.setName( "testUser1" );
        request.setAge(24);
        request.setEmail("user1@test.com");
        request.setCity("rome");
        request.setPassword("testUser1Password");
        return request;
    }

}
