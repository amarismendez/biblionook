package org.example.controllers;

import org.example.domain.LibraryItemService;
import org.example.domain.Result;
import org.example.models.LibraryItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/library_items")
public class LibraryItemController {
    private final LibraryItemService service;

    public LibraryItemController(LibraryItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<LibraryItem> findAll() {
        return service.findAll();
    }

    @GetMapping("/{appUserId}")
    public List<LibraryItem> findByAppUserId(@PathVariable int appUserId) {
        return service.findByAppUserId(appUserId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody LibraryItem libraryItem) {
        Result<LibraryItem> result = service.create(libraryItem);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
        }
        return ErrorResponse.build(result);
    }

    @PutMapping("/{libraryItemId}")
    public ResponseEntity<Object> update(@PathVariable int libraryItemId, @RequestBody LibraryItem libraryItem) {
        if (libraryItemId != libraryItem.getLibraryItemId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<LibraryItem> result = service.update(libraryItem);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{libraryItemId}")
    public ResponseEntity<Object> deleteById(@PathVariable int libraryItemId) {
        if (service.deleteById(libraryItemId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
