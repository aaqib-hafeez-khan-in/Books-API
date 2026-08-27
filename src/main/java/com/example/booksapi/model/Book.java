package com.example.booksapi.model;

import lombok.Data;
import java.util.List;

@Data
public class Book {
    private String key;
    private String title;
    private List<String> authors;
    private String publishYear;
    private List<String> subjects;
    private String isbn;
    private String coverUrl;
    private String description;
}
