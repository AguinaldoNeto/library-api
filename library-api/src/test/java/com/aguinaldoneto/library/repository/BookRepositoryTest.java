package com.aguinaldoneto.library.repository;


import com.aguinaldoneto.library.api.model.entity.Book;
import com.aguinaldoneto.library.api.repository.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    //Utilizado para criar o cenário JPA
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com isbn informado.")
    public void shouldReturnTrueWhenAIsbnExistsInDataBase() {

        //cenario
        Book book = createNewBook();
        entityManager.persist(book);

        String isbn = "123";

        //execucao
        boolean existIsbn = repository.existsByIsbn(isbn);

        //verificacao
        assertThat(existIsbn).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando NÃO existir um livro na base com isbn informado.")
    public void shouldReturnFalseWhenAIsbnNotExistsInDataBase() {

        //cenario
        Book book = createNewBook();

        String isbn = "123";

        //execucao
        boolean existIsbn = repository.existsByIsbn(isbn);

        //verificacao
        assertThat(existIsbn).isFalse();
    }

    private static Book createNewBook() {
        return Book.builder()
                .title("As aventuras")
                .author("Aguinaldo")
                .isbn("123").build();
    }

}
