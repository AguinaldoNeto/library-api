package com.neto.libraryapi.repository;

import com.neto.libraryapi.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base de dados com isbn informado.")
    public void returnTrueWhenIsbnExists() {
        //cenario
        String isbn = "123";
        Object book = Book.builder().title("Aventuras").author("Fulano").isbn(isbn).build();
        entityManager.persist(book);

        //ação
        boolean existsIsbn = repository.existsByIsbn(isbn);

        //verificação
        Assertions.assertThat(existsIsbn).isTrue();

    }

    @Test
    @DisplayName("Deve retornar falso quando NÃO existir um livro na base de dados com isbn informado.")
    public void returnFalseWhenIsbnNotExists() {
        //cenario
        String isbn = "123";

        //ação
        boolean existsIsbn = repository.existsByIsbn(isbn);

        //verificação
        Assertions.assertThat(existsIsbn).isFalse();

    }

}
