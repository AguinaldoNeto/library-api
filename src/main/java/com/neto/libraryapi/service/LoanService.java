package com.neto.libraryapi.service;

import com.neto.libraryapi.entity.Loan;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

}
