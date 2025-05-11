package mp.project.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mp.project.example.dto.SearchResultDTO;
import mp.project.example.service.Embedding.SearchService;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestEmbeddingController {

    private final SearchService searchService;

    @GetMapping("/testEmbedding")
    public ResponseEntity<List<SearchResultDTO>> testSemanticSearch() {

        System.out.println("📥 /semantic-search API 호출됨");
        // ✨ 임의의 키워드 3개
        List<String> keywords = List.of("인공지능", "전기차", "환경");

        // 유사 뉴스 top 5개 반환
        List<SearchResultDTO> results = searchService.searchWithGeneratedSentences(keywords, 5);

        System.out.println("✅ 검색 결과 개수: " + results.size());

        return ResponseEntity.ok(results);
    }
}
