package org.example.data;

import org.example.models.LibraryItem;

import java.util.List;

public interface LibraryItemRepository {
    List<LibraryItem> findAll();

    List<LibraryItem> findByAppUserId(int appUserId);

    LibraryItem create(LibraryItem libraryItem);

    boolean update(LibraryItem libraryItem);

    boolean deleteById(int libraryItemId);
}
