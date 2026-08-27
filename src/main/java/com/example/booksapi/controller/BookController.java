package com.example.booksapi.controller;

import com.example.booksapi.model.BookResponse;
import com.example.booksapi.service.OpenLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {
    
    @Autowired
    private OpenLibraryService openLibraryService;
    
    @GetMapping("/search")
    public ResponseEntity<BookResponse> searchBooks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String filterType,
            @RequestParam(required = false) String filterValue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        BookResponse response = openLibraryService.searchBooks(query, filterType, filterValue, page, size);
        return ResponseEntity.ok(response);
    }
}
