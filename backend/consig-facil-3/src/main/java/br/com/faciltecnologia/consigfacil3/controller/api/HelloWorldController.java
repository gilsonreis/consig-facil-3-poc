package br.com.faciltecnologia.consigfacil3.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloWorldController {

    @GetMapping
    public String sayHello() {
        return "Hello, World! O sistema está operando com Java 21.";
    }
}
