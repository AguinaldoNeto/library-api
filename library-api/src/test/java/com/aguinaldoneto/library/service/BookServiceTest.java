package com.aguinaldoneto.library.service;

import com.aguinaldoneto.library.api.model.entity.Book;
import com.aguinaldoneto.library.api.repository.BookRepository;
import com.aguinaldoneto.library.api.service.BookService;
import com.aguinaldoneto.library.api.service.impl.BookServiceImpl;
import com.aguinaldoneto.library.exception.BusinessException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
        Book book = createNewBook();

        Book bookReturn = createNewBook();

        when(repository.existsByIsbn(anyString())).thenReturn(false);
        when(repository.save(book)).thenReturn(bookReturn);

        //ação
        Book savedBook = service.save(book);

        //verificacao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
        assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve lançar erro de negócio ao tentar salvar um lvro com isbn já cadastrado no BD.")
    public void shouldThrowAErrorWhenIsbnAlreadyExistsInDataBase() {
        Book book = createNewBook();

        //estou mockando o repo, portanto seu valor é default e o padrão para boolean é false. O when está jogando para TRUE e passando o cenário que já temos um isbn cadastrado na DB.
        when(repository.existsByIsbn(anyString())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable( () -> service.save(book));

        assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("ISBN já cadastrado.");
        verify(repository, never()).save(book);
    }

    private static Book createNewBook() {
        return Book.builder()
                .id(1L)
                .author("Aguinaldo")
                .title("Uma cachorra linda - Regina")
                .isbn("123456")
                .build();
    }


}
