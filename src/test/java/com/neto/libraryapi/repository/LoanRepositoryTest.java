package com.neto.libraryapi.repository;

import com.neto.libraryapi.entity.Book;
import com.neto.libraryapi.entity.Loan;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

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
}
