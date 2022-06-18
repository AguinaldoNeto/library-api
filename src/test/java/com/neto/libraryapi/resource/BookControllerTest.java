package com.neto.libraryapi.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neto.libraryapi.dto.BookDTO;
import com.neto.libraryapi.entity.Book;
import com.neto.libraryapi.exception.BusinessException;
import com.neto.libraryapi.service.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.regex.Matcher;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);

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
        BDDMockito.given(service.save(Mockito.any(Book.class)))
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

    private BookDTO createNewBook() {
        return BookDTO.builder().title("Meu livro.").author("autor").isbn("123456").build();
    }
}
