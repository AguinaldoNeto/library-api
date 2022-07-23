package com.neto.libraryapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${application.mail.default-remetent}")
    private String remetent;

    private final JavaMailSender javaMailSender;

    @Override
    public void sendMails(String mensagem, List<String> mailsList) {

        String[] mails = mailsList.toArray(new String[mailsList.size()]);

        SimpleMailMessage mailCore = new SimpleMailMessage();
        mailCore.setFrom(remetent);
        mailCore.setSubject("Livro com empréstimo em atraso");
        mailCore.setText(mensagem);
        mailCore.setTo(mails);

        javaMailSender.send(mailCore);
    }
}
