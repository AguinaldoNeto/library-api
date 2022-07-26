package com.neto.libraryapi.resource;

import com.neto.libraryapi.dto.BookDTO;
import com.neto.libraryapi.dto.LoanDTO;
import com.neto.libraryapi.entity.Book;
import com.neto.libraryapi.entity.Loan;
import com.neto.libraryapi.service.BookService;
import com.neto.libraryapi.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Api("Api Book")
public class BookController {

    private BookService service;
    private ModelMapper modelMapper;
    private LoanService loanService;


    @ApiOperation("Create a book")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create (@RequestBody @Valid BookDTO bookDTO) {
        Book entity = modelMapper.map(bookDTO, Book.class);
        entity = service.save(entity);

        return modelMapper.map(entity, BookDTO.class);
    }

    @ApiOperation("Obtains all books")
    @GetMapping
    public Page<BookDTO> find(BookDTO dto, Pageable pageRequest) {
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = service.find(filter, pageRequest);
        List<BookDTO> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements());
    }

    @ApiOperation("Obtains a book by id")
    @GetMapping("/{id}")
    public BookDTO getById(@PathVariable Long id) {
        return service
                .getById(id)
                .map( book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow( () -> new ResponseStatusException(NOT_FOUND));

    }

    @ApiOperation("Delete a book by id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Book book = service
                .getById(id)
                .orElseThrow( () -> new ResponseStatusException(NOT_FOUND));

        service.delete(book);
    }

    @ApiOperation("Update a book by id")
    @PutMapping("/{id}")
    public BookDTO update(@PathVariable Long id, BookDTO dto) {
        return service
                .getById(id)
                .map(book -> {
                    book.setTitle(dto.getTitle());
                    book.setAuthor(dto.getAuthor());
                    Book bookAtualizado = service.update(book);
                    return modelMapper.map(bookAtualizado, BookDTO.class);
                })
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

    }

    @GetMapping("{id}/loans")
    public Page<LoanDTO> loansByBook(@PathVariable Long id, Pageable pageable) {
        Book book = service
                .getById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        Page<Loan> result = loanService.getLoansByBook(book, pageable);
        List<LoanDTO> list = result.getContent()
                .stream()
                .map(loan -> {

                    Book loanBook = loan.getBook();
                    BookDTO bookDTO = modelMapper.map(loanBook, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);
                    loanDTO.setBook(bookDTO);
                    return loanDTO;

                }).collect(Collectors.toList());

        return new PageImpl<LoanDTO>(list, pageable, result.getTotalElements());
    }

}
