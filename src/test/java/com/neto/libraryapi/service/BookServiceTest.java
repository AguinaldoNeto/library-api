package com.neto.libraryapi.service;

import com.neto.libraryapi.entity.Book;
import com.neto.libraryapi.exception.BusinessException;
import com.neto.libraryapi.repository.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;
    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {
        //cenario
        Book book = Book.builder()
                .author("Aguinaldo Neto")
                .title("Como apanhar da TI todos os dias")
                .isbn("123321")
                .build();

        when(repository.existsByIsbn(anyString())).thenReturn(false);
        when(repository.save(book)).thenReturn(Book.builder()
                .id(1L)
                .author("Aguinaldo Neto")
                .title("Como apanhar da TI todos os dias")
                .isbn("123321")
                .build());
        //execucao
        Book savedBook = service.save(book);

        //verificacao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getId()).isEqualTo(1L);
        assertThat(savedBook.getAuthor()).isEqualTo("Aguinaldo Neto");
        assertThat(savedBook.getTitle()).isEqualTo("Como apanhar da TI todos os dias");
        assertThat(savedBook.getIsbn()).isEqualTo("123321");

    }

    @Test
    @DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABookDuplicatedIsbn() {
        Book book = Book.builder()
                .author("Aguinaldo Neto")
                .title("Como apanhar da TI todos os dias")
                .isbn("123321")
                .build();

        when(repository.existsByIsbn(anyString())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.");

        Mockito.verify(repository, never()).save(book);

    }
}
