package com.form.cliente.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FormTestController {

    @GetMapping("/form/hello")
    public String hello() {
        return "Hola desde el sistema externo de formularios!";
    }
}
