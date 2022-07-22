package com.neto.libraryapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "costumer", length = 100)
    private String costumer;

    @JoinColumn(name = "id_book")
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Book book;

    @Column(name = "loan_date")
    private LocalDate loanDate;

    @Column(name = "returned")
    private Boolean returned;
}
