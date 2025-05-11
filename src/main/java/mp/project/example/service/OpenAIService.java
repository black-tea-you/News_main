package mp.project.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    private final WebClient webClient;
    

    public OpenAIService(@Value("${spring.ai.openai.api-key}") String apiKey) {
        System.out.println("🔑 API 키: " + apiKey); // 여기에 추가!
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type","application/json")
                .build();
    }
    public String testSummary() {
        String testInput = "대한민국은 2025년에도 여전히 인공지능 기술이 빠르게 발전하고 있으며, 관련 산업도 큰 주목을 받고 있다.";
    
        String result = summarizeText(testInput);
        System.out.println("🧪 테스트 요약 결과: " + result);
        return result;

    }
    

    public String summarizeText(String input) {
    try {
        var messages = java.util.List.of(
            Map.of("role", "system", "content", "너는 뉴스 요약 도우미야."),
            Map.of("role", "user", "content", "다음 기사를 3줄로 요약해줘:\n" + input)
    );

        String response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(Map.of(
                        "model", "gpt-3.5-turbo",
                        "messages", messages,
                        "max_tokens", 300,
                        "temperature", 0.7
                ))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // 결과 JSON에서 요약문 추출
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);
        return root.path("choices").get(0).path("message").path("content").asText().trim();


    } catch (Exception e) {
        System.err.println("❌ OpenAI 요약 오류: " + e.getMessage());
        return "요약 실패: " + e.getMessage();  // 또는 null 반환도 가능
    }
}

/** 키워드 기반 예시 문장 생성 기능 추가 */
public List<String> generateExamples(String keyword) {
    try {
        var messages = List.of(
                Map.of("role", "system", "content", "너는 뉴스 키워드 생성 도우미야."),
                Map.of("role", "user", "content", keyword + "와 관련된 뉴스 제목 문장을 짧게 2개 만들어줘. 장르는 전부 기술 관련 단어야")
        );

        String response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(Map.of(
                        "model", "gpt-3.5-turbo",
                        "messages", messages,
                        "max_tokens", 200,
                        "temperature", 0.7
                ))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);
        String content = root.path("choices").get(0).path("message").path("content").asText();

        // 줄 단위로 분리
        return Arrays.stream(content.split("\n"))
                .map(s -> s.replaceAll("^[0-9]+[.\\)]\\s*", "").trim()) // "1." 또는 "1) " 제거
                .filter(s -> !s.isBlank())
                .limit(2)
                .toList();

    } catch (Exception e) {
        System.err.println("❌ GPT 문장 생성 오류: " + e.getMessage());
        return List.of();
    }
}

}