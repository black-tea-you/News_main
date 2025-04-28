package mp.project.example.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class HomeController {
    @GetMapping("/")
    public String redirectToRegister(){
        return "redirect:/register";
    }
}
