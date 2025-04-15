package mp.project.example.dto;

import lombok.Data;

@Data
public class NewsDTO {
    private String title;
    private String link;
    private String description;
    private String category;
    private String keyword;
    private String date;
}
