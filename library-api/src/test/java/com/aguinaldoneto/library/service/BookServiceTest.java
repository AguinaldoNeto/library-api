package com.aguinaldoneto.library.service;

import com.aguinaldoneto.library.api.model.entity.Book;
import com.aguinaldoneto.library.api.repository.BookRepository;
import com.aguinaldoneto.library.api.service.BookService;
import com.aguinaldoneto.library.api.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    @MockBean
    BookRepository repository;
    BookService service;

    @BeforeEach
    public void setUp() {
        // para realizar a intejeção de dependências com o serviço
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void shouldSaveABook() {
        //cenario
        Book book = Book.builder()
                .id(1L)
                .author("Aguinaldo")
                .title("Uma cachorra linda - Regina")
                .isbn("123456")
                .build();

        Book bookReturn = Book.builder()
                .id(1L)
                .author("Aguinaldo")
                .title("Uma cachorra linda - Regina")
                .isbn("123456")
                .build();

        when(repository.save(book)).thenReturn(bookReturn);

        //ação
        Book savedBook = service.save(book);

        //verificacao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
        assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
    }



}
