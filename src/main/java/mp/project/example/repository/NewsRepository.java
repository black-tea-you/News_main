package mp.project.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mp.project.example.domain.News;

public interface NewsRepository extends JpaRepository<News, Long>{
    
}
