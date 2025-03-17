package com.phamcongvinh.springrestfull.controller;

import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.phamcongvinh.springrestfull.module.User;
import com.phamcongvinh.springrestfull.service.EmailService;
import com.phamcongvinh.springrestfull.service.TokenService;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    // @GetMapping("/email")
    // public String sendSimpleEmail() {
    // this.emailService.sendSimpleEmail();
    // return "ok";
    // }
    // @GetMapping("/email")
    // public String sendSimpleEmail() {
    // this.emailService.sendEmailSync("tinguyen554@gmail.com", "Test email",
    // "<h1><p>Hello</p></h1>", false, true);
    // return "ok";
    // }

    @GetMapping("/email")
    @Scheduled(cron = "*/60 * * * * *")
    public String sendSimpleEmail() {

        this.emailService.sendEmailFromTemplateSync(
                "tinguyen554@gmail.com",
                "Thư cảm ơn",
                "sendemailbs",
                "vinh");
        return "ok";
    }

}
