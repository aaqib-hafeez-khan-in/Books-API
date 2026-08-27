package com.example.booksapi.model;

import lombok.Data;
import java.util.List;

@Data
public class BookResponse {
    private List<Book> books;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
