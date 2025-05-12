package mp.project.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mp.project.example.domain.News;
import mp.project.example.dto.NewsDTO;
import mp.project.example.service.NewsCrawlingService;

@RestController
@RequestMapping("/api/news")
public class NewsSearchController {

    private final NewsCrawlingService newsCrawlingService;

    public NewsSearchController(NewsCrawlingService newsCrawlingService) {
        this.newsCrawlingService = newsCrawlingService;
    }

    @GetMapping("/headline")
    public ResponseEntity<List<NewsDTO>> getLatestHeadlines(@RequestParam(defaultValue = "5") int size) {
    List<NewsDTO> newsList = newsCrawlingService.getLatestHeadlines(size);
    return ResponseEntity.ok(newsList);
    }
    

    @GetMapping("/search")
    public ResponseEntity<List<NewsDTO>> searchNewsForOneDay(
            /**@RequestParam String category,
            @RequestParam String keyword,
            @RequestParam String date, // yyyy-MM-dd**/
            @RequestParam String lstcode,
            @RequestParam String start,
            @RequestParam String end 
            //String categoryUrl = "https://zdnet.co.kr/news/?lstcode=0000";
            //String start = "2025-04-28";
            //String end = "2025-04-30";

    ) throws Exception {
        System.out.println("📥 [요청 도착] /api/news/search");
        //categotyUrl 임베딩을 통해 정하는 방법 ?
        List<NewsDTO> newsList = newsCrawlingService.crawlCategoryAndDate(lstcode, start, end);
        return ResponseEntity.ok(newsList);
    }

    @GetMapping("/all")
    public ResponseEntity<List<News>> getAllArticles() {
        return ResponseEntity.ok(newsCrawlingService.getAllArticles());
    }
}