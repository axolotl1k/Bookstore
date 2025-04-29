package com.example.bookstore.service;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.BookFilterDto;

import java.util.List;

public interface BookService {
    List<BookDto> findByFilter(BookFilterDto f);
    BookDto findById(Long bookId);
    BookDto create(BookDto dto);
    BookDto update(Long bookId, BookDto dto);
    void delete(Long bookId);
}
