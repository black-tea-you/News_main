package mp.project.example.service;

import java.util.ArrayList;
import java.util.List;

//import javax.lang.model.util.Elements;
//import javax.swing.text.Document;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.io.IOException;
import mp.project.example.domain.News;
import mp.project.example.domain.User;
//import jakarta.xml.bind.Element;
import mp.project.example.dto.NewsDTO;
import mp.project.example.repository.NewsRepository;
import mp.project.example.repository.UserRepository;




@Service
public class NewsCrawlingService {
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final OpenAIService openAIService;
    public NewsCrawlingService(NewsRepository newsRepository,UserRepository userRepository,OpenAIService openAIService) {
        this.newsRepository = newsRepository;
        this.userRepository=userRepository;
        this.openAIService=openAIService;
    }

    public List<NewsDTO> crawlOneDay(String category, String keyword, String date) throws IOException, java.io.IOException {
        List<NewsDTO> results = new ArrayList<>();

        String formattedDate = date.replace("-", "");
        String url = "https://news.naver.com/main/list.naver?mode=LSD&mid=sec&sid1=105&date=" + formattedDate;
//일단은 하루치만 크롤링 나중에 연도별로 고를수있게 
        Document doc = Jsoup.connect(url).get();
        Elements articles = doc.select("ul.type06_headline li a[href]");
        System.out.println("📰 존재하는 기사 수: " + articles.size());
        int maxSummaryCount=5;
        int count=0;
        for (Element article : articles) {
            if(count>=maxSummaryCount)break; // 최대 5개 기사만 크롤링 
            String title = article.text();
            String link = article.attr("href");
            // 썸네일 이미지 추출
            Element imgTag=article.selectFirst("img");
            String urlimg=imgTag!=null?imgTag.attr("src"):null;

            // 본문 추출
            String body = extractNewsBody(link);
            System.out.println("🔗 기사 링크: " + link);
            System.out.println("📄 본문: " + body.substring(0, Math.min(100, body.length())));
            if (body.length() < 10) continue;
            
            //요약
            String summary=openAIService.summarizeText(body);

            //DB 저장 
            News news=new News();
            news.setTitle(title);
            news.setDescription(body.length() > 1000 ? body.substring(0, 1000) + "..." : body);
            news.setSummary(summary);
            news.setLink(link);
            news.setDate(date);
            news.setUrlimg(urlimg);
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            User user=userRepository.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not found"));
            news.setUser(user);
            newsRepository.save(news);


            NewsDTO dto = new NewsDTO();
            dto.setTitle(title);
            dto.setLink(link);
            dto.setDescription(body.length() > 500 ? body.substring(0, 500) + "..." : body);
            dto.setCategory(category);
            dto.setKeyword(keyword);
            dto.setDate(date);
            dto.setUrlimg(urlimg);
            dto.setSummary(summary);

            results.add(dto);
            count++;
        }

        return results;
    }

    private String extractNewsBody(String link) {
        try {
            Document newsDoc = Jsoup.connect(link).get();
    
            // ✅ 본문 셀렉터 여러 개 시도
            List<String> selectors = List.of(
                "div#dic_area",               // 일반 기사
                "div#newsct_article",         // 모바일 버전 / 최근 기사
                "div.article_body",           // 스포츠, 연예
                "div.content",                // 일부 외부 기사
                "div#articeBody",             // 오타 맞춤용
                "div#articleBodyContents"     // 예전 네이버 뉴스
            );
    
            for (String selector : selectors) {
                Element content = newsDoc.selectFirst(selector);
                if (content != null) {
                    return content.text();
                }
            }
    
            System.out.println("❌ 본문 셀렉터 실패: " + link);
            return "";
        } catch (Exception e) {
            System.out.println("❌ 크롤링 오류: " + link + " → " + e.getMessage());
            return "";
        }
    }
    
}