package mp.project.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class OpenAIService {

    private final WebClient webClient;
    

    public OpenAIService(@Value("${openai.api.key}") String apiKey) {
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
        System.out.println("🧠 요약 요청: " + input.substring(0, Math.min(100, input.length())) + "...");
       
         Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", new Object[]{
                        Map.of("role", "system", "content", "다음 내용을 5줄 정도로 한국어로 요약해줘."),
                        Map.of("role", "user", "content", input)
                }
        );

        try {
            Map response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                 .block();

            System.out.println("✅ OpenAI 응답 수신: " + response);

            var message = ((Map)((Map)((java.util.List)response.get("choices")).get(0)).get("message")).get("content");
            return message.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ 요약 실패 상세: " + e.getMessage());
            return "[요약 실패]";
        }
    }
}