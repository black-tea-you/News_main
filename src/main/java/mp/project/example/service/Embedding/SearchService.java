package mp.project.example.service.Embedding;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import mp.project.example.domain.News;
import mp.project.example.dto.NewsDTO;
import mp.project.example.dto.SearchResultDTO;
import mp.project.example.repository.NewsRepository;
import mp.project.example.service.OpenAIService;

@Service
public class SearchService {
    private final EmbeddingModel embedder;
    private final NewsRepository newsRepo;
    private final OpenAIService openAIService;

    public SearchService(EmbeddingModel embedder, NewsRepository newsRepo, OpenAIService openAIService) {
        this.embedder = embedder;
        this.newsRepo = newsRepo;
        this.openAIService = openAIService;
    }

    /** GPT 문장 자동 생성 포함 → 검색 실행 */
    public List<SearchResultDTO> searchWithGeneratedSentences(List<String> keywords, int topK) {
        System.out.println("🧠 KEYWORD: " + keywords);
        Map<String, List<String>> sentenceMap = new HashMap<>();
        for (String kw : keywords) {
            List<String> gptSents = openAIService.generateExamples(kw);
            sentenceMap.put(kw, gptSents);
            System.out.println("📝 GPT 문장 (" + kw + "): " + gptSents);
        }

        return searchByKeywordsAndSentences(keywords, sentenceMap, topK);
    }

    /** 코사인 유사도 기반 검색 */
    public List<SearchResultDTO> searchByKeywordsAndSentences(
            List<String> keywords,
            Map<String, List<String>> genSentences,
            int topK) {

        List<String> queries = new ArrayList<>();
        for (String kw : keywords) {
            List<String> sents = genSentences.getOrDefault(kw, List.of());
            if (sents.isEmpty()) {
                queries.add(kw);
            } else {
                queries.addAll(sents);
            }
        }
        if (queries.isEmpty()) return List.of();

        List<float[]> qVecs = embedder.embed(queries);
        List<News> allNews = newsRepo.findAll();

        List<SearchResultDTO> results = new ArrayList<>();

        System.out.println("Check Embedding:");

        for (News news : allNews) {
            byte[] embeddingBytes = news.getEmbedding();
        if (embeddingBytes == null) {
        continue; // 임베딩이 없는 뉴스는 건너뜀
        }
            float[] titleVec = bytesToFloats(news.getEmbedding());
            double sumSim = 0;
            for (float[] qVec : qVecs) {
                sumSim += cosine(titleVec, qVec);
            }
            float avgSim = (float) (sumSim / qVecs.size());
            results.add(new SearchResultDTO(NewsDTO.fromEntity(news), avgSim));
        }

        return results.stream()
                .sorted((r1, r2) -> Float.compare(r2.getScore(), r1.getScore()))
                .limit(topK)
                .toList();
    }

    private float[] bytesToFloats(byte[] bytes) {
        FloatBuffer fb = ByteBuffer.wrap(bytes).asFloatBuffer();
        float[] arr = new float[fb.remaining()];
        fb.get(arr);
        return arr;
    }

    private float cosine(float[] a, float[] b) {
        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        
        double result = dot / (Math.sqrt(na) * Math.sqrt(nb));
        if (Double.isNaN(result)) {
        System.out.println("유사도 계산에서 NaN 발생 → 0으로 대체");
        return 0.0f;
        }
        return (float) result;
    }
}

// @Service
// public class SearchService {
//     private final EmbeddingModel embedder;
//     private final NewsRepository newsRepo;

//     public SearchService(EmbeddingModel embedder, NewsRepository newsRepo) {
//         this.embedder = embedder;
//         this.newsRepo = newsRepo;
//     }
    
//     /**
//      * 키워드와 GPT 생성 문장을 모두 임베딩하여
//      * 뉴스 제목 임베딩과 비교한 뒤 평균 유사도로 상위 topK 개 반환
//      *
//      * @return List of SearchResultDTO containing NewsDTO and similarity score
//      */
//     public List<SearchResultDTO> searchByKeywordsAndSentences(
//             List<String> keywords,
//             Map<String, List<String>> genSentences,
//             int topK) {
    
//         // 1) 키워드별 실제 임베딩할 텍스트 리스트 구성
//         List<String> queries = new ArrayList<>();
//         for (String kw : keywords) {
//             List<String> sents = genSentences.getOrDefault(kw, List.of());
//             if (sents.isEmpty()) {
//                 queries.add(kw);
//             } else {
//                 queries.addAll(sents);
//             }
//         }
//         if (queries.isEmpty()) {
//             return List.of();
//         }
    
//         // 2) 한 번에 모든 쿼리 임베딩 생성
//         List<float[]> qVecs = embedder.embed(queries);
    
//         // 3) DB에서 모든 뉴스 로드
//         List<News> allNews = newsRepo.findAll();
    
//         // 4) 각 뉴스마다 평균 cosine 유사도 계산 및 DTO 변환
//         List<SearchResultDTO> results = new ArrayList<>();
//         for (News news : allNews) {
//             float[] titleVec = bytesToFloats(news.getEmbedding());
//             double sumSim = 0;
//             for (float[] qVec : qVecs) {
//                 sumSim += cosine(titleVec, qVec);
//             }
//             float avgSim = (float) (sumSim / qVecs.size());
//             // record result with score
//             results.add(new SearchResultDTO(NewsDTO.fromEntity(news), avgSim));
//         }
    
//         // 5) score 내림차순 정렬 후 상위 topK
//         return results.stream()
//                 .sorted((r1, r2) -> Float.compare(r2.getScore(), r1.getScore()))
//                 .limit(topK)
//                 .toList();
//     }
    
//     private float[] bytesToFloats(byte[] bytes) {
//         FloatBuffer fb = ByteBuffer.wrap(bytes).asFloatBuffer();
//         float[] arr = new float[fb.remaining()];
//         fb.get(arr);
//         return arr;
//     }
    
//     private float cosine(float[] a, float[] b) {
//         double dot = 0, na = 0, nb = 0;
//         for (int i = 0; i < a.length; i++) {
//             dot += a[i] * b[i];
//             na += a[i] * a[i];
//             nb += b[i] * b[i];
//         }
//         return (float) (dot / (Math.sqrt(na) * Math.sqrt(nb)));
//     }
// }
