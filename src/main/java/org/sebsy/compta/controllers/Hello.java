package org.sebsy.compta.controllers;

import org.sebsy.compta.service.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class Hello {

    private final MessageService messageService;

    public Hello(MessageService _messageService){
        this.messageService = _messageService;
    }

    @GetMapping("/hello")
    public String sayHello() {
        return messageService.greetings();
    }
}
