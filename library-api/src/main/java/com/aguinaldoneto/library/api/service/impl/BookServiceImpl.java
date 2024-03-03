package com.aguinaldoneto.library.api.service.impl;

import com.aguinaldoneto.library.api.model.entity.Book;
import com.aguinaldoneto.library.api.repository.BookRepository;
import com.aguinaldoneto.library.api.service.BookService;
import com.aguinaldoneto.library.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    public BookRepository repository;

    public BookServiceImpl (BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        boolean isNotValidIsbn = repository.existsByIsbn(book.getIsbn());

        if (isNotValidIsbn) {
            throw new BusinessException("ISBN já cadastrado.");
        }

        return repository.save(book);
    }

}
