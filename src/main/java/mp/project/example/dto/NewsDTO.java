package mp.project.example.dto;

import lombok.Data;
import mp.project.example.domain.News;

@Data
public class NewsDTO {
    private String title;
    private String link;
    private String description;
    private String category;
    private String keyword;
    private String date;
    private String urlimg;
    private String summary;

    public static NewsDTO fromEntity(News news) {
        NewsDTO dto = new NewsDTO();
        dto.setTitle(news.getTitle());
        dto.setLink(news.getLink());
        dto.setDescription(news.getDescription());
        dto.setDate(news.getDate());
        dto.setUrlimg(news.getUrlimg());
        dto.setSummary(news.getSummary());
        return dto;
    }
}
