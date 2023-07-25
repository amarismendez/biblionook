package org.example.domain;

import org.example.data.AppUserRepository;
import org.example.models.AppUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class AppUserServiceTest {
    @Autowired
    AppUserService service;

    @MockBean
    AppUserRepository repository;

    // LOAD USER TESTS
    @Test
    void shouldLoadUserByUsername() {
        AppUser expected = new AppUser(1, "testName", "testPass", true, List.of("USER"));
        when(repository.findByUsername("testName")).thenReturn(expected);

        UserDetails result = service.loadUserByUsername("testName");
        assertEquals(expected, result);
    }

    @Test
    void shouldNotLoadUserByMissingUsername() {
        when(repository.findByUsername("testName")).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> {service.loadUserByUsername("testName");});
        // to assert an exception being thrown:
        // https://stackoverflow.com/questions/40268446/junit-5-how-to-assert-an-exception-is-thrown
    }

    @Test
    void shouldCreateValidUser() {
        AppUser output = new AppUser(1, "test@test.com", "P@ssw0rd!", true, List.of("USER"));
        when(repository.create(any())).thenReturn(output);

        Result<AppUser> result = service.create("test@test.com", "P@ssw0rd!");
        assertTrue(result.isSuccess());
        assertEquals(output, result.getPayload());
    }

    @Test
    void shouldNotCreateUserWithMissingName() {
        AppUser output = new AppUser(1, "test@test.com", "P@ssw0rd!", true, List.of("USER"));
        when(repository.create(any())).thenReturn(output);

        Result<AppUser> result = service.create("  ", "P@ssw0rd!");
        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
        assertTrue(result.getMessages().contains("username is required"));
    }

    @Test
    void shouldNotCreateUserWithNullPassword() {
        AppUser output = new AppUser(1, "test@test.com", "P@ssw0rd!", true, List.of("USER"));
        when(repository.create(any())).thenReturn(output);

        Result<AppUser> result = service.create("test@test.com", null);
        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
        assertTrue(result.getMessages().contains("password is required"));
    }

    @Test
    void shouldNotCreateUserWithUsernameGreaterThan50Chars() {
        AppUser output = new AppUser(1, "test@test.com", "P@ssw0rd!", true, List.of("USER"));
        when(repository.create(any())).thenReturn(output);

        Result<AppUser> result = service.create("test@test.comtest@test.comtest@test.comtest@test.comtest@test.com", "P@ssw0rd!");
        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
        assertTrue(result.getMessages().contains("username must be less than 50 characters"));
    }

    @Test
    void shouldNotCreateUserWithInvalidPassword() {
        AppUser output = new AppUser(1, "test@test.com", "P@ssw0rd!", true, List.of("USER"));
        when(repository.create(any())).thenReturn(output);

        Result<AppUser> result = service.create("test@test.com", "    ");
        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
        assertTrue(result.getMessages().contains("password must be at least 8 character and contain a digit, a letter, and a non-digit/non-letter"));
    }

    @Test
    void shouldNotCreateUserWithNonUniqueUsername() {
        when(repository.create(any())).thenThrow(DuplicateKeyException.class);

        Result<AppUser> result = service.create("sally@jones.com", "P@ssw0rd!");
        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
        assertTrue(result.getMessages().contains("The provided username already exists"));
    }
}