package br.com.faciltecnologia.consigfacil3.controller.web.admin.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginAdminWebAction {

    @GetMapping("/admin/auth/login")
    public String execute() {
        return "admin/login";
    }
}
