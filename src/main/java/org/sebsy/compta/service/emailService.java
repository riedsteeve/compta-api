package org.sebsy.compta.service;

import org.springframework.stereotype.Service;

@Service
public class emailService implements MessageService{

    public String greetings() {
        return "Hello by Email";
    }
}
