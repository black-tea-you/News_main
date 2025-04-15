package mp.project.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.io.IOException;
import mp.project.example.dto.NewsDTO;
import mp.project.example.service.NewsCrawlingService;

@RestController
@RequestMapping("/api/news")
public class NewsSearchController {

    private final NewsCrawlingService newsCrawlingService;

    public NewsSearchController(NewsCrawlingService newsCrawlingService) {
        this.newsCrawlingService = newsCrawlingService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<NewsDTO>> searchNewsForOneDay(
            @RequestParam String category,
            @RequestParam String keyword,
            @RequestParam String date // yyyy-MM-dd
    ) throws IOException, java.io.IOException {
        List<NewsDTO> newsList = newsCrawlingService.crawlOneDay(category, keyword, date);
        return ResponseEntity.ok(newsList);
    }
}