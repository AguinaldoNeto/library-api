package com.aguinaldoneto.library.api.resource;

import com.aguinaldoneto.library.api.dto.BookDTO;
import com.aguinaldoneto.library.api.model.entity.Book;
import com.aguinaldoneto.library.api.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static final String BOOK_API = "/api/books";
    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    public void createBookTest() throws Exception {

        //requisição
        BookDTO dto = BookDTO.builder()
                .title("As aventuras")
                .author("Aguinaldo")
                .isbn("001")
                .build();

        // objeto
        Book savedBook = Book.builder()
                .id(1L)
                .title("As aventuras")
                .author("Aguinaldo")
                .isbn("001")
                .build();

        given(service.save(any(Book.class))).willReturn(savedBook);

        String json = new ObjectMapper().writeValueAsString(dto);

        // para acessar a requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // o que eu espero receber na requisição
        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()));

    }

    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para a criação de um livro")
    public void shouldThrowErrorValidationWhenTheresNotEnoughDataToCreateABook() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        // para acessar a requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }
}
