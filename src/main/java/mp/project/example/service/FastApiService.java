package mp.project.example.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FastApiService {

    private final WebClient webClient;

    private final String BASE_URL = "http://localhost:8000";//"https://your-fastapi.onrender.com"; 현재 local로 테스트 중

    public List<String> getTitles() {
        return webClient.get()
                .uri(BASE_URL + "/titles")
                .retrieve()
                .bodyToMono(Map.class)
                .map(res -> (List<String>) res.get("titles"))
                .block();
    }

    public List<String> getTopKeywords(List<String> titles) {
        Map<String, Object> request = Map.of("titles", titles);
        return webClient.post()
                .uri(BASE_URL + "/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .map(res -> (List<String>) res.get("keywords"))
                .block();
    }
}