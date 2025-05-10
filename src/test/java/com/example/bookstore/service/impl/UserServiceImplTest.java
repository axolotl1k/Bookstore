package com.example.bookstore.service.impl;

import com.example.bookstore.dto.UserDto;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "SMTP_USER=test@gmail.com",
        "SMTP_PASS=testpass"
})
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        var dto = new UserDto("testuser", "test@example.com", "password");
        userService.register(dto);

        // Установка автентифікації через username, не User
        var auth = new TestingAuthenticationToken("testuser", null, "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void register_shouldPersistUser() {
        var dto = new UserDto("newuser", "new@example.com", "secret");
        userService.register(dto);

        User saved = userRepository.findByUsername("newuser").orElseThrow();
        assertNotNull(saved.getId());
        assertEquals("newuser", saved.getUsername());
    }

    @Test
    void getCurrentUser_shouldReturnAuthenticatedUser() {
        User current = userService.getCurrentUser();
        assertNotNull(current);
        assertEquals("testuser", current.getUsername());
    }

    @Test
    void deleteCurrentUser_shouldRemoveUser() {
        userService.deleteCurrentUser();
        assertTrue(userRepository.findByUsername("testuser").isEmpty());
    }

    @Test
    void findAllUsers_shouldReturnListIncludingTestUser() {
        List<User> users = userService.findAllUsers();
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("testuser")));
    }
}
