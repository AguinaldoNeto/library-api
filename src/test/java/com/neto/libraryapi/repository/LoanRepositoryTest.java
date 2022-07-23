package com.neto.libraryapi.repository;

import com.neto.libraryapi.entity.Book;
import com.neto.libraryapi.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRepository repository;

    @Test
    @DisplayName("Deve verificar se existe empréstimo não devolvido para o livro.")
    public void existsByBookAndNotReturned() {

        //cenario
        Book book = Book.builder().isbn("123").build();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).costumer("Aguinaldo").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        //execução
        boolean exists = repository.existsByBookAndNotReturned(book);

        assertThat(exists).isTrue();

    }

    @Test
    @DisplayName("Deve buscar um empréstimo pelo isbn do livro ou costumer.")
    public void findByBookIsbyOrCostumer() {

        //cenario
        Book book = Book.builder().isbn("123").build();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).costumer("Aguinaldo").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        Page<Loan> result = repository.findByBookIsnOrCostumer(
                book.getIsbn(),
                loan.getCostumer(),
                PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10L);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().contains(loan));

    }

    @Test
    @DisplayName("Deve obter empréstimos cuja a data de empréstimo for maior ou igual a três dias atrás e não retornados")
    public void findByLoanDateLessThanAndNotReturned() {

        Loan loan = Loan.builder().costumer("Aguinaldo").loanDate(LocalDate.now().minusDays(5)).build();
        entityManager.persist(loan);

        List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(loan.getLoanDate().minusDays(4));

        assertThat(result).hasSize(1).contains(loan);
    }

    @Test
    @DisplayName("Deve obter empréstimos cuja a data de empréstimo for menor ou igual a três dias atrás e não retornados")
    public void notFindByLoanDateLessThanAndNotReturned() {

        Loan loan = Loan.builder().costumer("Aguinaldo").loanDate(LocalDate.now().minusDays(3)).build();
        entityManager.persist(loan);

        List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(loan.getLoanDate().minusDays(4));

        assertThat(result).isEmpty();
    }
}
