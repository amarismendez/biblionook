package org.example.domain;

import org.example.data.LibraryItemJdbcTemplateRepository;
import org.example.models.BookStatus;
import org.example.models.LibraryItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class LibraryItemServiceTest {
    @Autowired
    LibraryItemService service;

    @MockBean
    LibraryItemJdbcTemplateRepository repository;

    @Test
    void findByAppUserId() {
        List<LibraryItem> existing = List.of(makeLibraryItem());
        when(repository.findByAppUserId(1)).thenReturn(existing);

        List<LibraryItem> libraryItems = service.findByAppUserId(1);

        assertEquals(existing, libraryItems);
        assertEquals(1, libraryItems.size());
    }

    @Test
    void shouldNotFindMissing() {
        when(repository.findByAppUserId(50)).thenReturn(new ArrayList<>());

        List<LibraryItem> libraryItems = service.findByAppUserId(50);

        assertEquals(0, libraryItems.size());
    }

    @Test
    void shouldCreate() {
        LibraryItem libraryItem = makeLibraryItem();
        when(repository.create(any())).thenReturn(libraryItem);

        Result<LibraryItem> result = service.create(libraryItem);

        assertTrue(result.isSuccess());
        assertNotNull(result.getPayload());
    }

    @Test
    void shouldNotCreateInvalid() {
        LibraryItem libraryItem = makeInvalidLibraryItem();
        Result<LibraryItem> result = service.create(libraryItem);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Book selection is required."));
        assertTrue(result.getMessages().contains("Rating must be between 0 and 10."));
        assertTrue(result.getMessages().contains("Book status is required."));
    }

    @Test
    void shouldNotCreateDuplicate() {
        LibraryItem libraryItem = makeLibraryItem();
        libraryItem.setLibraryItemId(15);
        when(repository.findByAppUserId(1)).thenReturn(List.of(libraryItem));

        Result<LibraryItem> result = service.create(makeLibraryItem());

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("This book already exists in your library."));
    }

    @Test
    void shouldUpdate() {
        LibraryItem libraryItem = makeLibraryItem();
        when(repository.update(libraryItem)).thenReturn(true);

        Result<LibraryItem> result = service.update(libraryItem);

        assertEquals(ResultType.SUCCESS, result.getType());
        assertEquals(libraryItem, result.getPayload());
    }

    @Test
    void shouldNotUpdateMissing() {
        LibraryItem libraryItem = makeLibraryItem();
        libraryItem.setLibraryItemId(50);
        when(repository.update(libraryItem)).thenReturn(false);

        Result<LibraryItem> result = service.update(libraryItem);

        assertEquals(ResultType.NOT_FOUND, result.getType());
        assertNull(result.getPayload());
        assertTrue(result.getMessages().contains("There is no library item with ID #50"));
    }

    @Test
    void shouldNotUpdateInvalid() {
        LibraryItem libraryItem = makeInvalidLibraryItem();
        Result<LibraryItem> result = service.update(libraryItem);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Book selection is required."));
        assertTrue(result.getMessages().contains("Rating must be between 0 and 10."));
        assertTrue(result.getMessages().contains("Book status is required."));
    }

    @Test
    void shouldNotUpdateDuplicate() {
        LibraryItem libraryItem = makeLibraryItem();
        libraryItem.setLibraryItemId(1);
        LibraryItem duplicate = makeLibraryItem();
        duplicate.setLibraryItemId(5);

        when(repository.findByAppUserId(1)).thenReturn(List.of(libraryItem));
        Result<LibraryItem> result = service.update(duplicate);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("This book already exists in your library."));
    }

    @Test
    void shouldDeleteById() {
        when(repository.deleteById(1)).thenReturn(true);
        assertTrue(service.deleteById(1));
    }

    @Test
    void shouldNotDeleteByMissingId() {
        when(repository.deleteById(50)).thenReturn(false);
        assertFalse(service.deleteById(50));
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

    private LibraryItem makeInvalidLibraryItem() {
        LibraryItem libraryItem = new LibraryItem();
        libraryItem.setIsbn(null);
        libraryItem.setRating(11);
        libraryItem.setDescription("    ");
        libraryItem.setStatus(null);
        libraryItem.setAppUserId(1);

        return libraryItem;
    }
}

