package mp.project.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mp.project.example.domain.News;

public interface NewsRepository extends JpaRepository<News, Long>{
    // 시작일(startDate) 이상, 종료일(endDate) 이하 범위 내 기사만 날짜 내림차순으로 조회 찾는 속도 너무 느려서 search 방식 조정
    List<News> findAllByDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(
        String startDate, String endDate);
}
