package com.neto.libraryapi.dto;

import com.neto.libraryapi.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanDTO {

    private Long id;

    @NotEmpty
    private String isbn;

    @NotEmpty
    private String costumer;

    @NotEmpty
    private BookDTO book;

    private String email;
}
