package com.neto.libraryapi.service;

import com.neto.libraryapi.entity.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book any);

    Optional<Book> getById(Long id);

    void delete(Book Book);

    Book update(Book book);

}
