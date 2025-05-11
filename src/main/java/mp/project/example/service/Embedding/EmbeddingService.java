package mp.project.example.service.Embedding;

import java.nio.ByteBuffer;

import org.springframework.ai.embedding.EmbeddingModel;    // 변경: 올바른 EmbeddingModel 인터페이스
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mp.project.example.domain.News;
import mp.project.example.repository.NewsRepository;

@Service
public class EmbeddingService {
    private final EmbeddingModel embedder;
    private final NewsRepository newsRepo;

    public EmbeddingService(EmbeddingModel embedder, NewsRepository newsRepo) {
        this.embedder = embedder;
        this.newsRepo = newsRepo;
    }

    @Transactional
    public byte[] indexTitle(Long newsId, String title) {
        // 1) GPT Embedding API 호출 → float[] 반환
        float[] vec = embedder.embed(title);

        // 2) float[] → byte[] 직렬화
        ByteBuffer buf = ByteBuffer.allocate(Float.BYTES * vec.length);
        for (float f : vec) {
            buf.putFloat(f);
        }
        byte[] bytes = buf.array();

        // 3) 엔티티에 embedding 설정
        News n = newsRepo.findById(newsId).orElseThrow();
        n.setEmbedding(bytes);
        // 변경 감지로 자동 업데이트

        return bytes;
    }
}
