package com.neto.libraryapi.service;

import com.neto.libraryapi.dto.LoanFilterDTO;
import com.neto.libraryapi.entity.Book;
import com.neto.libraryapi.entity.Loan;
import com.neto.libraryapi.exception.BusinessException;
import com.neto.libraryapi.repository.LoanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if (repository.existsByBookAndNotReturned(loan.getBook())) {
            throw new BusinessException("Book already loaned");
        }
        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return repository.save(loan);
    }

    @Override
    public Page<Loan> find(LoanFilterDTO loanFilterDTO, Pageable pageable) {
        return repository.findByBookIsnOrCostumer(loanFilterDTO.getIsbn(), loanFilterDTO.getIsbn(), pageable);
    }

    @Override
    public Page<Loan> getLoansByBook(Book book, Pageable pageable) {
        return repository.findByBook(book, pageable);
    }

    @Override
    public List<Loan> getAllLoans() {
        final Integer firstDayOfLoanLate = 4;
        LocalDate dayOfLoan = LocalDate.now().minusDays(firstDayOfLoanLate);
        return repository.findByLoanDateLessThanAndNotReturned(dayOfLoan);
    }
}
