package com.neto.libraryapi.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neto.libraryapi.dto.BookDTO;
import com.neto.libraryapi.entity.Book;
import com.neto.libraryapi.exception.BusinessException;
import com.neto.libraryapi.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";
    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    public void createBookTest() throws Exception {
        //Cenário
        BookDTO bookDTO = BookDTO.builder().title("Meu livro.").author("autor").isbn("123456").build();

        Book savedBook = Book.builder().id(10L).title("Meu livro.").author("autor").isbn("123456").build();

        given(service.save(Mockito.any(Book.class))).willReturn(savedBook);

        String json = new ObjectMapper().writeValueAsString(bookDTO);

        // ação
        MockHttpServletRequestBuilder request = post(BOOK_API)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .accept(MediaType.APPLICATION_JSON)
                                            .content(json);

        //MockMvcResultMatchers
        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("id").value(10L))
                .andExpect(jsonPath("title").value(bookDTO.getTitle()))
                .andExpect(jsonPath("author").value(bookDTO.getAuthor()))
                .andExpect(jsonPath("isbn").value(bookDTO.getIsbn()));

    }

    @Test
    @DisplayName("Deve lançar um erro de validação quando não houver dados suficientes para criação do livro.")
    public void createInvalidBookTest() throws Exception {

        //Cenário
        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        // ação
        MockHttpServletRequestBuilder request = post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //verificacao
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }

    @Test
    @DisplayName("Deve lançar um erro ao tentar cadastrar um livro com isbn já cadastrado por outro.")
    public void createBookWithDuplicatedIsbn() throws Exception {

        BookDTO bookDTO = createNewBook();
        String json = new ObjectMapper().writeValueAsString(bookDTO);

        String mensagemErro = "Isbn já cadastrado.";
        given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(mensagemErro));

        // ação
        MockHttpServletRequestBuilder request = post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(mensagemErro));
    }

    @Test
    @DisplayName("Deve obter informações de um livro.")
    public void getBookDetailsTest() throws Exception {
        //Cenário
        Long id = 1L;

        Book book = Book.builder()
                .id(id)
                .title("Aventuras")
                .author("Fulano")
                .isbn("123")
                .build();

        given(service.getById(id)).willReturn(Optional.of(book));

        // ação
        MockHttpServletRequestBuilder request = get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        //MockMvcResultMatchers
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("title").value(book.getTitle()))
                .andExpect(jsonPath("author").value(book.getAuthor()))
                .andExpect(jsonPath("isbn").value(book.getIsbn()));

    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro não existir")
    public void bookNotFound() throws Exception {
        //cenario
        given(service.getById(anyLong())).willReturn(Optional.empty());

        // ação
        MockHttpServletRequestBuilder request = get(BOOK_API.concat("/" + 10))
                .accept(MediaType.APPLICATION_JSON);

        // verificação
        mvc
                .perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve deletar um livro.")
    public void deleteBookTest() throws Exception{
        //cenario
        Long id = 1L;

        Book book = Book.builder()
                .id(id)
                .build();

        given(service.getById(id)).willReturn(Optional.of(book));

        // ação
        MockHttpServletRequestBuilder request = delete(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // verificação
        mvc
                .perform(request)
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro não for encotrado.")
    public void deleteInexistentBookTest() throws Exception{
        //cenario
        given(service.getById(anyLong())).willReturn(Optional.empty());

        // ação
        MockHttpServletRequestBuilder request = delete(BOOK_API.concat("/" + 10L))
                .accept(MediaType.APPLICATION_JSON);

        // verificação
        mvc
                .perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve atualizar um livro.")
    public void updateBookTest() throws Exception{
        //cenario
        Long id = 1L;

        Book book = Book.builder()
                .title("Aventuras")
                .author("Fulano")
                .isbn("123")
                .build();

        Book bookAtualizado = Book.builder()
                .id(id)
                .title("Aventuras atualizado")
                .author("Fulano atualizado")
                .isbn("123")
                .build();

        given(service.getById(id)).willReturn(Optional.of(book));
        given(service.update(book)).willReturn(bookAtualizado);

        String json = new ObjectMapper().writeValueAsString(book);

        // ação
        MockHttpServletRequestBuilder request =
                put(BOOK_API.concat("/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // verificação
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("title").value("Aventuras atualizado"))
                .andExpect(jsonPath("author").value("Fulano atualizado"))
                .andExpect(jsonPath("isbn").value("123"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um livro inexistente.")
    public void updateInexistentBookTest() throws Exception{
        //cenario
        Book book = Book.builder()
                .title("Aventuras")
                .author("Fulano")
                .isbn("123")
                .build();

        given(service.getById(anyLong())).willReturn(Optional.empty());

        String json = new ObjectMapper().writeValueAsString(book);

        // ação
        MockHttpServletRequestBuilder request = put(BOOK_API.concat("/" + 10L))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // verificação
        mvc
                .perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve filtrar livros.")
    public void findBooksTest() throws Exception {
        Long id = 1L;

        Book book = Book.builder()
                .id(id)
                .title("Aventuras")
                .author("Fulano")
                .isbn("123")
                .build();

        BDDMockito.given(service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(
                    new PageImpl<Book>(Arrays.asList(book),
                            PageRequest.of(0, 100),
                            1));

        String queryString = String.format("?title=%s&author=%s&page=0&size=100",
                book.getTitle(), book.getAuthor());

        // ação
        MockHttpServletRequestBuilder request = get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        // verificação
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));

    }

    private BookDTO createNewBook() {
        return BookDTO.builder().title("Meu livro.").author("autor").isbn("123456").build();
    }
}
