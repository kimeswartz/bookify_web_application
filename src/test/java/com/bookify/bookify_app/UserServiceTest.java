package com.bookify.bookify_app;

// ********************************************************************************************
// * UserServiceTest verifies the business logic of UserService around registration.          *
// *                                                                                          *
// * Test setup:                                                                              *
// *  - Uses Mockito to mock UserRepository (no database involved).                           *
// *  - Uses a real BCryptPasswordEncoder to ensure password hashing is validated.            *
// *                                                                                          *
// * Covered scenarios:                                                                       *
// *  - Registering a new user should create a user with hashed password, default OWNER role, *
// *    and matching email.                                                                   *
// *  - Registering with a duplicate email should throw IllegalArgumentException.             *
// *                                                                                          *
// * WHY: Ensures that critical user registration logic (unique email + password hashing)     *
// * works correctly and safely before relying on persistence or integration tests.           *
// ********************************************************************************************

import com.bookify.bookify_app.model.User;
import com.bookify.bookify_app.repository.UserRepository;
import com.bookify.bookify_app.service.UserService;
import com.bookify.bookify_app.util.UserRole;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private final UserRepository repo = mock(UserRepository.class);
    private final UserService service = new UserService(repo, new BCryptPasswordEncoder(12));

    @Test
    void shouldCreateUser_whenRegister() {
        when(repo.existsByEmail("test@x.com")).thenReturn(false);
        when(repo.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User u = service.registerUser("test@x.com", "secret");
        assertEquals("test@x.com", u.getEmail());
        assertTrue(new BCryptPasswordEncoder().matches("secret", u.getPasswordHash()));
        assertTrue(u.getRoles().contains(UserRole.OWNER));
    }

    @Test
    void shouldThrowError_whenDuplicateEmail() {
        when(repo.existsByEmail("exists@x.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.registerUser("exists@x.com", "pw"));
    }
}
