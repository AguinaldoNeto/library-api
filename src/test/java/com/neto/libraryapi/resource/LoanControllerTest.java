package com.neto.libraryapi.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neto.libraryapi.dto.LoanDTO;
import com.neto.libraryapi.dto.LoanFilterDTO;
import com.neto.libraryapi.dto.ReturnedLoanDTO;
import com.neto.libraryapi.entity.Book;
import com.neto.libraryapi.entity.Loan;
import com.neto.libraryapi.exception.BusinessException;
import com.neto.libraryapi.service.BookService;
import com.neto.libraryapi.service.LoanService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    static final Long ID = 1L;

    @Autowired
    MockMvc mvc;

    @MockBean
    LoanService loanService;

    @MockBean
    BookService bookService;

    @Test
    @DisplayName("Deve realizar  um empréstimo de um livro")
    public void createLoanTest() throws Exception {

        //cenário
        LoanDTO dto = LoanDTO.builder()
                .isbn("001").costumer("Aguinaldo Neto").build();

        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1L).isbn("123").build();

        Loan costumerSaved = Loan.builder()
                        .id(1L).costumer("Aguinaldo").book(book).loanDate(LocalDate.now()).build();

        given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));

        given(loanService.save(any(Loan.class))).willReturn(costumerSaved);

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

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer o empréstimo de um livro inexistente")
    public void invalidIsbnCreateLoanTest() throws Exception {

        //cenário
        LoanDTO dto = LoanDTO.builder()
                .isbn("001").costumer("Aguinaldo Neto").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1L).isbn("").build();

        given(bookService.getBookByIsbn("")).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"));

    }

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer o empréstimo de um livro já emprestado")
    public void loanedBookErrorOnCreateLoanTest() throws Exception {

        //cenário
        LoanDTO dto = LoanDTO.builder()
                .isbn("123").costumer("Aguinaldo Neto").build();

        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1L).isbn("123").build();

        given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));
        given(loanService.save(any(Loan.class))).willThrow(new BusinessException("Book already loaned"));

        MockHttpServletRequestBuilder request = post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book already loaned"));

    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar retornar um livro inexistente")
    public void returnInexistenteBookTest() throws Exception {

        ReturnedLoanDTO returnedLoan = ReturnedLoanDTO.builder().build();

        String json = new ObjectMapper().writeValueAsString(returnedLoan);

        given(loanService.getById(anyLong())).willReturn(Optional.empty());

        //execução
        MockHttpServletRequestBuilder request = patch(LOAN_API.concat("/" + ID))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve filtrar livros")
    public void findLoan() throws Exception {

        final Long ID_LOAN = 1L;

        Book book = Book.builder().id(92L).isbn("352").build();

        Loan loan = Loan.builder()
                .id(ID_LOAN).book(book).costumer("Cost").loanDate(LocalDate.now()).build();

        given(loanService.find(any(LoanFilterDTO.class), any(Pageable.class))).willReturn(
                new PageImpl<Loan>(Arrays.asList(loan),
                        PageRequest.of(0, 10), 1));

        String queryString = String.format(
                "?isbn=%s&costumer=%s&page=0&size=0",
                //"?title=%&author=%s&page=0&size=100"
                book.getIsbn(), loan.getCostumer());

        MockHttpServletRequestBuilder request = get(LOAN_API)
                .content(queryString);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(10))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }



}
