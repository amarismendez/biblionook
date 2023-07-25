package org.example.data;

import org.example.models.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AppUserJdbcTemplateRepositoryTest {
    @Autowired
    private AppUserJdbcTemplateRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    static boolean hasSetup = false;

    @BeforeEach
    void setup() {
        if (!hasSetup) {
            hasSetup = true;
            jdbcTemplate.update("call set_known_good_state();");
        }
    }

    // TESTS

    @Test
    void shouldFindAll(){
        List<AppUser> result = repository.findAll();
        assertNotNull(result);
        assertTrue(result.size() >= 2);
        assertTrue(result.get(0).getAuthorities().size() > 0);
    }
    @Test
    void findByUsername() {
        AppUser result = repository.findByUsername("john@smith.com");
        assertNotNull(result);
    }

    @Test
    void shouldNotFindByMissingUsername() {
        AppUser result = repository.findByUsername("missing@username.com");
        assertNull(result);
    }

    @Test
    void create() {
        AppUser newUser = new AppUser(0, "test@test.com", "testPassword", true, List.of("USER"));
        repository.create(newUser);
        assertNotNull(repository.findByUsername("test@test.com"));
        assertTrue(repository.findByUsername("test@test.com").getAppUserId() > 0);
    }

    @Test
    void update() {
        AppUser toUpdate = repository.findByUsername("sally@jones.com");
        toUpdate.setAuthorities(List.of("USER", "ADMIN"));
        repository.update(toUpdate);
        AppUser afterUpdate = repository.findByUsername("sally@jones.com");
        assertEquals(2, afterUpdate.getAuthorities().size());
    }
}