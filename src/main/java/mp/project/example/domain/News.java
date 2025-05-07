package mp.project.example.domain;

import java.security.Timestamp;

import org.hibernate.annotations.Columns;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "news")
@Data
public class News {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**  @ManyToOne // 이 엔티티(News)는 하나의 User에 여러 개가 속할 수 있다는 의미 
    @JoinColumn(name = "user_id") //외래 키로 사용할 컬럼 이름을 지정하는 부분 
    private User user; //이 필드를 통해서 News에서 직접 User 정보에 접근 가능**/
    
    
    @Column(name = "create_time")
    private String date;
    private String title;
    @Lob
    private String description;
    private String link;
    private String urlimg;
    @Lob
    private String summary;
    private String category;
    private String embedding;
}
