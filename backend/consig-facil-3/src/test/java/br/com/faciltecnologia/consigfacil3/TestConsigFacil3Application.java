package br.com.faciltecnologia.consigfacil3;

import org.springframework.boot.SpringApplication;

public class TestConsigFacil3Application {

    public static void main(String[] args) {
        SpringApplication.from(ConsigFacil3Application::main).with(TestcontainersConfiguration.class).run(args);
    }

}
