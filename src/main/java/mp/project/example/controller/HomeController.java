package mp.project.example.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/news")  //api로 시작하는 모든 URL 요청을 처리 
public class HomeController {
    @GetMapping("/home")  //홈화면 
    public Map<String, String> home() {
        return Map.of("message", "홈 화면입니다.");
    }
}
