package com.neto.libraryapi.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neto.libraryapi.dto.LoanDTO;
import com.neto.libraryapi.entity.Book;
import com.neto.libraryapi.entity.Loan;
import com.neto.libraryapi.service.BookService;
import com.neto.libraryapi.service.LoanService;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    LoanService service;

    @MockBean
    BookService bookService;

    @Test
    @DisplayName("Deve realizar  um empréstimo de um livro")
    public void createLoanTest() throws Exception {

        //cenário
        LoanDTO dto = LoanDTO.builder()
                .isbn("001").costumer("Aguinaldo Neto")
                .build();

        Book book = Book.builder().id(1L).isbn("123").build();

        Loan costumerSaved = Loan.builder()
                        .id(1L).costumer("Aguinaldo").book(book).loanDate(LocalDate.now())
                        .build();

        given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));

        given(service.save(any(Loan.class))).willReturn(costumerSaved);

        String json = new ObjectMapper().writeValueAsString(dto);

        //ação
        MockHttpServletRequestBuilder request = post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        //verificacao
        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));

    }


}
