package mp.project.example.dto;

import lombok.Data;

@Data
public class SearchResultDTO{
private NewsDTO news;
private float score;

    public SearchResultDTO(NewsDTO news, float score) {
        this.news = news;
        this.score = score;
    }
}
