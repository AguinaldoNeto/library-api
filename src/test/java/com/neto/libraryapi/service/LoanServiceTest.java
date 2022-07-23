package com.neto.libraryapi.service;

import com.neto.libraryapi.dto.LoanFilterDTO;
import com.neto.libraryapi.entity.Book;
import com.neto.libraryapi.entity.Loan;
import com.neto.libraryapi.exception.BusinessException;
import com.neto.libraryapi.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    LoanRepository repository;

    LoanService service;

    @BeforeEach
    public void setUp() {
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um empréstimo")
    public void saveLoanTest() throws Exception {

        Book book = Book.builder().id(10L).build();

        Loan loan = Loan.builder()
                .book(book).costumer("Aguinaldo Neto").loanDate(LocalDate.now()).build();

        Loan loanDto = Loan.builder()
                .id(1L)
                .book(book).costumer("Aguinaldo Neto").loanDate(LocalDate.now()).build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
        when(repository.save(loan)).thenReturn(loanDto);

        Loan loanSaved = service.save(loan);

        assertThat(loanSaved.getId()).isNotNull();
        assertThat(loanSaved.getId()).isEqualTo(1L);
        assertThat(loanSaved.getBook().getId()).isEqualTo(10L);
        assertThat(loanSaved.getCostumer()).isEqualTo("Aguinaldo Neto");
        assertThat(loanSaved.getLoanDate()).isEqualTo(loan.getLoanDate());

    }

    @Test
    @DisplayName("Deve lançar um erro de negócio ao tentar salvar um empréstimo com livro já emprestado.")
    public void loanedBookSaveTest() throws Exception {

        Book book = Book.builder().id(10L).build();

        Loan loanDto = Loan.builder()
                .id(1L)
                .book(book).costumer("Aguinaldo Neto").loanDate(LocalDate.now()).build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable exception = catchThrowable(() -> service.save(loanDto));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        verify(repository, never()).save(loanDto);

    }

    @Test
    @DisplayName("Deve buscar um empréstimo pelo ID")
    public void getLoanTest() throws Exception {

        Book book = Book.builder().id(12L).isbn("123").build();
        Loan loan = Loan.builder().id(1L).book(book).loanDate(LocalDate.now()).costumer("Aguinaldo").build();

        when(repository.findById(anyLong())).thenReturn(Optional.of(loan));

        Optional<Loan> result = service.getById(loan.getId());

        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getCostumer()).isEqualTo("Aguinaldo");
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());
        assertThat(result.get().getBook().getId()).isEqualTo(12L);

        verify(repository, times(1)).findById(loan.getId());

    }

    @Test
    @DisplayName("Deve atualizar um empréstimo")
    public void updateLoanTest() {

        Loan loan = Loan.builder().id(12L).returned(true).build();

        when(repository.save(loan)).thenReturn(loan);

        Loan result = service.save(loan);

        assertThat(result.getId()).isNull();
        assertThat(result.getId()).isEqualTo(12L);
        assertThat(result.getReturned()).isTrue();

        verify(repository, times(1)).save(loan);

    }

    @Test
    @DisplayName("Deve filtrar empréstimos pelas propriedades")
    public void findLoan() throws Exception {

        final Long ID_LOAN = 1L;

        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().isbn("123").costumer("teste").build();

        Loan loan = Loan.builder().id(ID_LOAN).build();

        List<Loan> list = Arrays.asList(loan);
        PageRequest pageRequest = PageRequest.of(0, 10);

        PageImpl<Loan> page = new PageImpl<>(list, pageRequest, list.size());
        when(repository.findByBookIsnOrCostumer(
                anyString(),
                anyString(),
                any(PageRequest.class)))
                .thenReturn(page);

        Page<Loan> result = service.find(loanFilterDTO, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(list);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

}
