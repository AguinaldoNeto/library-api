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
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


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
    @DisplayName("Deve salvar um livro.")
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
    @DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com isbn duplicado.")
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

    @Test
    @DisplayName("Deve obter um livro por id.")
    public void getByIdTest() {
        //cenario
        Long id = 1L;

        Book book = Book.builder()
                .id(1L)
                .author("Aguinaldo Neto")
                .title("Como apanhar da TI todos os dias")
                .isbn("123321")
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(book));

        //execucao
        Optional<Book> foundBook = service.getById(id);

        //verificacao
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(1L);
        assertThat(foundBook.get().getAuthor()).isEqualTo("Aguinaldo Neto");
        assertThat(foundBook.get().getTitle()).isEqualTo("Como apanhar da TI todos os dias");
        assertThat(foundBook.get().getIsbn()).isEqualTo("123321");

    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por Id quando ele não existe na base.")
    public void bookNotFoundByIdTest() {
        //cenario
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        //execucao
        Optional<Book> book = service.getById(id);

        //verificacao
        assertThat(book.isEmpty());

    }

    @Test
    @DisplayName("Deve deletar um livro por id.")
    public void deleteBookTest() {
        //cenario
        Long id = 1L;
        Book book = Book.builder().id(id).build();

        //ação
        assertDoesNotThrow( () -> service.delete(book));

        //verificação
        verify(repository, times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve retornar uma mensagem de erro ao tentar deletar um livro sem id.")
    public void deleteBookWithoutIdTest() {
        //cenario
        Book book = Book.builder().build();

        //ação
        assertThrows(IllegalArgumentException.class, () -> service.delete(book), "Book id cant be null.");

        //verificação
        verify(repository, never()).delete(book);

    }

    @Test
    @DisplayName("Deve atualizar um livro por id.")
    public void updateBookTest() {
        //cenario
        Long id = 1L;
        String isbn = "123";

        Book bookCadastrado = Book.builder()
                .id(id)
                .title("Título")
                .author("Autor")
                .isbn(isbn)
                .build();

        Book bookAtualizado = Book.builder()
                .id(id)
                .title("Título atualizado")
                .author("Autor atualizado")
                .isbn(isbn)
                .build();

        //ação
        when(repository.save(bookCadastrado)).thenReturn(bookAtualizado);
        Book book = service.update(bookCadastrado);

        //verificação
        assertThat(book.getId()).isEqualTo(1L);
        assertThat(book.getTitle()).isEqualTo("Título atualizado");
        assertThat(book.getAuthor()).isEqualTo("Autor atualizado");
    }

    @Test
    @DisplayName("Deve retornar uma mensagem de erro ao tentar atualizar um livro inexistente.")
    public void updateInvalidBookTest() {
        //cenario
        Book book = new Book();

        //ação
        assertThrows(IllegalArgumentException.class, () -> service.update(book), "Book id cant be null.");

        //verificação
        verify(repository, never()).save(book);

    }

    @Test
    @DisplayName("Deve filtrar livros por titulo ou autor.")
    public void findBookByTitleOrAuthorTest() {
        //cenario

        Book book = Book.builder()
                .id(1L).title("Título").author("Autor").isbn("123").build();

        List<Book> lista = Arrays.asList(book);
        Pageable pageRequest = PageRequest.of(0, 25);

        Page<Book> page = new PageImpl<Book>(lista, pageRequest, 1);

        when(repository.findAll(any(Example.class), any(PageRequest.class)))
                .thenReturn(page);

        //execucao
        Page<Book> result = service.find(book, pageRequest);

        //verificacao
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(25);


    }

    @Test
    @DisplayName("Deve oter um livro pelo isbn.")
    public void getBookyIsbn() {

        Book book = Book.builder()
                .id(1L).title("Título").author("Autor").isbn("123").build();

        when(repository.findByIsbn(book.getIsbn())).thenReturn(Optional.of(book));

        Optional<Book> bookValid = service.getBookByIsbn(book.getIsbn());

        assertThat(bookValid.isPresent()).isTrue();
        assertThat(bookValid.get().getIsbn()).isEqualTo("123");

        verify(repository, times(1)).findByIsbn(book.getIsbn());

    }


}
