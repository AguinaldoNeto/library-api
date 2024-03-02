package com.aguinaldoneto.library.api.repository;

import com.aguinaldoneto.library.api.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
