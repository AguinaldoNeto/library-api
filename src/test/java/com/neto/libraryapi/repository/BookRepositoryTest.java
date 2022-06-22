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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        assertThat(existsIsbn).isTrue();

    }

    @Test
    @DisplayName("Deve retornar falso quando NÃO existir um livro na base de dados com isbn informado.")
    public void returnFalseWhenIsbnNotExists() {
        //cenario
        String isbn = "123";

        //ação
        boolean existsIsbn = repository.existsByIsbn(isbn);

        //verificação
        assertThat(existsIsbn).isFalse();

    }

    @Test
    @DisplayName("Deve obter um livro por id.")
    public void findByIdtest() {
        //cenario
        Book book = Book.builder().title("Aventuras").author("Fulano").isbn("123").build();
        entityManager.persist(book);

        //execucao
        Optional<Book> foundBook = repository.findById(book.getId());

        //verificacao
        assertThat(foundBook.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Deve salvar um livro.")
    public void saveBookTest() {
        //cenario
        Book book = Book.builder().title("Aventuras").author("Fulano").isbn("123").build();
        entityManager.persist(book);

        //execucao
        Book savedBook = repository.save(book);

        //verificacao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Aventuras");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
        assertThat(savedBook.getIsbn()).isEqualTo("123");

    }

    @Test
    @DisplayName("Deve deletar um livro.")
    public void deleteBookTest() {
        //cenario
        Book book = Book.builder().title("Aventuras").author("Fulano").isbn("123").build();
        entityManager.persist(book);

        //execucao
        Book foundBook = entityManager.find(Book.class, book.getId());
        repository.delete(foundBook);

        //verificacao
        Book deletedBook = entityManager.find(Book.class, book.getId());
        assertThat(deletedBook).isNull();

    }

}
