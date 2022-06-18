package com.neto.libraryapi.service;

import com.neto.libraryapi.entity.Book;
import com.neto.libraryapi.exception.BusinessException;
import com.neto.libraryapi.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }
    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Isbn já cadastrado.");
        }

        return repository.save(book);
    }
}
