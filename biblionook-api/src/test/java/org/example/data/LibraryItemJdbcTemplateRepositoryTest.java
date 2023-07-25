package org.example.data;

import org.example.models.BookStatus;
import org.example.models.LibraryItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LibraryItemJdbcTemplateRepositoryTest {
    @Autowired
    private LibraryItemJdbcTemplateRepository repository;

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

    @Test
    void shouldFindAll() {
        List<LibraryItem> result = repository.findAll();

        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    void shouldFindByAppUserId() {
        List<LibraryItem> result = repository.findByAppUserId(1);

        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    void shouldNotFindByNonexistentAppUserId() {
        List<LibraryItem> result = repository.findByAppUserId(50);

        assertEquals(0, result.size());
    }

    @Test
    void shouldCreate() {
        LibraryItem libraryItem = makeLibraryItem();

        LibraryItem result = repository.create(libraryItem);

        assertNotNull(result);
        assertEquals(5, result.getLibraryItemId());
    }

    @Test
    void shouldUpdate() {
        LibraryItem libraryItem = makeLibraryItem();
        libraryItem = repository.create(libraryItem); // now includes the id from db
        libraryItem.setDescription("updated description!!!");
        int id = libraryItem.getLibraryItemId();

        assertTrue(repository.update(libraryItem));
        assertTrue(libraryItem.getDescription().equals(repository.findAll().stream().filter(l -> l.getLibraryItemId() == id).findFirst().orElse(null).getDescription()));
    }

    @Test
    void shouldDeleteById() {
        LibraryItem libraryItem = repository.create(makeLibraryItem());
        int startingSize = repository.findAll().size();

        assertTrue(repository.deleteById(libraryItem.getLibraryItemId()));
        assertEquals(startingSize - 1, repository.findAll().size());
    }


    private LibraryItem makeLibraryItem() {
        LibraryItem libraryItem = new LibraryItem();
        libraryItem.setIsbn("abc123");
        libraryItem.setRating(2);
        libraryItem.setDescription("this book sucked");
        libraryItem.setStatus(BookStatus.COMPLETED);
        libraryItem.setAppUserId(1);

        return libraryItem;
    }
}