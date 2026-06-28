package br.com.faciltecnologia.consigfacil3.controller.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardAdminWebAction {

    @GetMapping("/admin/dashboard")
    public String execute() {
        return "admin/dashboard";
    }
}
