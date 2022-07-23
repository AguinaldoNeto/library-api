package com.neto.libraryapi.repository;

import com.neto.libraryapi.entity.Book;
import com.neto.libraryapi.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query(value = " select case when ( count(l.id) > 0 ) then true else false end "
            + " from Loan l "
            + " where l.book = :book "
            + " and ( l.returned is null or l.returned is false ) ")
    boolean existsByBookAndNotReturned(
            @Param("book") Book book);

    @Query(value = " select l from Loan as l "
            + "         join l.book as b "
            + " where b.isbn = :isbn "
            + " or l.costumer = :costumer ")
    Page<Loan> findByBookIsnOrCostumer(
            @Param("isbn") String isbn,
            @Param("costumer") String costumer,
            Pageable pageable
    );

    Page<Loan> findByBook(Book book, Pageable pageable);

    @Query(value = " select l from Loan l "
            + " where l.loanDate <= :dayOfLoan "
            + " and (l.returned is null or l.returned is false) ")
    List<Loan> findByLoanDateLessThanAndNotReturned(
            @Param("dayOfLoan") LocalDate dayOfLoan );
}
