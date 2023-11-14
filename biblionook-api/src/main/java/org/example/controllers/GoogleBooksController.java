package org.example.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/googlebooksapi")
public class GoogleBooksController {

    @RequestMapping(value = "/{searchString}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBooksBySearchString(@PathVariable String searchString) {
        String url = String.format("https://www.googleapis.com/books/v1/volumes?q=%s&key=%s", searchString, System.getenv("API_KEY"));
        RestTemplate restTemplate = new RestTemplate();

        String result = restTemplate.getForObject(url, String.class);

        return result;
    }

    @RequestMapping(value = "/isbn/{isbn}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBooksByIsbn(@PathVariable String isbn) {
        String url = String.format("https://www.googleapis.com/books/v1/volumes?q=+isbn:%s&key=%s", isbn, System.getenv("API_KEY"));
        RestTemplate restTemplate = new RestTemplate();

        String result = restTemplate.getForObject(url, String.class);

        return result;
    }

}
