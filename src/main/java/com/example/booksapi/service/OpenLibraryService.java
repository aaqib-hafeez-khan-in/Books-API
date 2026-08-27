package com.example.booksapi.service;

import com.example.booksapi.model.Book;
import com.example.booksapi.model.BookResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenLibraryService {
    
    private static final String OPEN_LIBRARY_API_URL = "https://openlibrary.org/search.json";
    
    @Autowired
    private RestTemplate restTemplate;
    
    public BookResponse searchBooks(String query, String filterType, String filterValue, 
                                  int page, int size) {
        String url = buildSearchUrl(query, filterType, filterValue, page, size);
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        return mapToBookResponse(response, page, size);
    }
    
    private String buildSearchUrl(String query, String filterType, String filterValue, 
                                int page, int size) {
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromHttpUrl(OPEN_LIBRARY_API_URL)
            .queryParam("page", page + 1) 
            .queryParam("limit", size);

        if (filterType != null && filterValue != null) {
            switch (filterType.toLowerCase()) {
                case "author":
                    builder.queryParam("author", filterValue);
                    break;
                case "subject":
                    builder.queryParam("subject", filterValue);
                    break;
                case "isbn":
                    builder.queryParam("isbn", filterValue);
                    break;
                default:
                    builder.queryParam("q", query);
            }
        } else if (query != null) {
            builder.queryParam("q", query);
        }

        return builder.build().toUriString();
    }
    
    private BookResponse mapToBookResponse(JsonNode response, int page, int size) {
        BookResponse bookResponse = new BookResponse();
        List<Book> books = new ArrayList<>();
        
        JsonNode docs = response.get("docs");
        if (docs != null && docs.isArray()) {
            for (JsonNode doc : docs) {
                Book book = new Book();
                book.setKey(doc.path("key").asText());
                book.setTitle(doc.path("title").asText());
                
                List<String> authors = new ArrayList<>();
                if (doc.has("author_name")) {
                    doc.get("author_name").forEach(author -> authors.add(author.asText()));
                }
                book.setAuthors(authors);
                
                if (doc.has("first_publish_year")) {
                    book.setPublishYear(doc.get("first_publish_year").asText());
                }
                
                List<String> subjects = new ArrayList<>();
                if (doc.has("subject")) {
                    doc.get("subject").forEach(subject -> subjects.add(subject.asText()));
                }
                book.setSubjects(subjects);
                
                if (doc.has("isbn")) {
                    book.setIsbn(doc.get("isbn").get(0).asText());
                }
                
                if (doc.has("cover_i")) {
                    String coverId = doc.get("cover_i").asText();
                    book.setCoverUrl("https://covers.openlibrary.org/b/id/" + coverId + "-L.jpg");
                }
                
                books.add(book);
            }
        }
        
        bookResponse.setBooks(books);
        bookResponse.setPage(page);
        bookResponse.setSize(size);
        bookResponse.setTotalElements(response.path("numFound").asLong());
        bookResponse.setTotalPages((int) Math.ceil(response.path("numFound").asDouble() / size));
        
        return bookResponse;
    }
}
