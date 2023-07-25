package org.example.domain;

import org.example.data.LibraryItemRepository;
import org.example.models.BookStatus;
import org.example.models.LibraryItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibraryItemService {
    private final LibraryItemRepository repository;

    public LibraryItemService(LibraryItemRepository repository) {
        this.repository = repository;
    }

    public List<LibraryItem> findAll() {
        return repository.findAll();
    }

    public List<LibraryItem> findByAppUserId(int appUserId) {
        return repository.findByAppUserId(appUserId);
    }

    public Result<LibraryItem> create(LibraryItem libraryItem) {
        Result<LibraryItem> result = validate(libraryItem);

        if (!result.isSuccess()) {
            return result;
        }

        libraryItem = repository.create(libraryItem); // attach id from database
        result.setPayload(libraryItem);

        return result;
    }

    public Result<LibraryItem> update(LibraryItem libraryItem) {
        Result<LibraryItem> result = validate(libraryItem);

        if (!result.isSuccess()) {
            return result;
        }

        if (!repository.update(libraryItem)) {
            result.addMessage(ResultType.NOT_FOUND,
                    String.format("There is no library item with ID #%s", libraryItem.getLibraryItemId()));
            return result;
        }

        result.setPayload(libraryItem);
        return result;
    }

    public boolean deleteById(int libraryItemId) {
        return repository.deleteById(libraryItemId);
    }

    private Result<LibraryItem> validate(LibraryItem libraryItem) {
        Result<LibraryItem> result = new Result<>();

        // isbn (book) is required.
        if (libraryItem.getIsbn() == null || libraryItem.getIsbn().isBlank()) {
            result.addMessage(ResultType.INVALID, "Book selection is required.");
        }


        // ratings must be between 0 and 10.
        if (libraryItem.getRating() < 0 || libraryItem.getRating() > 10) {
            result.addMessage(ResultType.INVALID, "Rating must be between 0 and 10.");
        }

        // no restrictions on description.

        // book status is required.
        if (libraryItem.getStatus() == null) {
            result.addMessage(ResultType.INVALID, "Book status is required.");
        }

        // each user cannot repeat a book in their library.
        for (LibraryItem li : repository.findByAppUserId(libraryItem.getAppUserId())) {
            if (li.getLibraryItemId() != libraryItem.getLibraryItemId() &&
                    li.getIsbn().equalsIgnoreCase(libraryItem.getIsbn())) {
                result.addMessage(ResultType.INVALID, "This book already exists in your library.");
            }
        }

        return result;
    }

}
