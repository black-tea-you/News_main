package mp.project.example.controller;

import lombok.RequiredArgsConstructor;
import mp.project.example.service.FastApiService;
import mp.project.example.service.OpenAIService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class KeywordController {

    private final FastApiService fastApiService;
    private final OpenAIService openAIService;

    @GetMapping("/keywords/final")
    public List<String> getFinalKeywords() {
        List<String> titles = fastApiService.getTitles();
        List<String> top10 = fastApiService.getTopKeywords(titles);
        return openAIService.refineTopKeywords(top10);  // 아래에 새로 추가
    }
}
