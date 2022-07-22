package com.neto.libraryapi.resource;

import com.neto.libraryapi.dto.LoanDTO;
import com.neto.libraryapi.dto.ReturnedLoanDTO;
import com.neto.libraryapi.entity.Book;
import com.neto.libraryapi.entity.Loan;
import com.neto.libraryapi.exception.BusinessException;
import com.neto.libraryapi.service.BookService;
import com.neto.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService service;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(CREATED)
    public Long create(@RequestBody LoanDTO dto) {
        Book book = bookService
                .getBookByIsbn(dto.getIsbn())
                .orElseThrow( () -> new ResponseStatusException(BAD_REQUEST, "Book not found for passed isbn"));

        Loan entity = Loan.builder()
                .costumer(dto.getCostumer())
                .book(book)
                .loanDate(LocalDate.now())
                .build();

        entity = service.save(entity);
        return entity.getId();
    }

    @PatchMapping("/{id}")
    @ResponseStatus(OK)
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto) {
        Loan loan = service
                .getById(id)
                .orElseThrow( () -> new ResponseStatusException(NOT_FOUND, "Book not found for passed isbn"));

        loan.setReturned(dto.getReturned());
        service.update(loan);
    }

}
