package mp.project.example.service;

import java.util.ArrayList;
import java.util.List;

//import javax.lang.model.util.Elements;
//import javax.swing.text.Document;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.io.IOException;
//import jakarta.xml.bind.Element;
import mp.project.example.dto.NewsDTO;




@Service
public class NewsCrawlingService {

    public List<NewsDTO> crawlOneDay(String category, String keyword, String date) throws IOException, java.io.IOException {
        List<NewsDTO> results = new ArrayList<>();

        String formattedDate = date.replace("-", "");
        String url = "https://news.naver.com/main/list.naver?mode=LSD&mid=sec&sid1=105&date=" + formattedDate;

        Document doc = Jsoup.connect(url).get();
        Elements articles = doc.select("ul.type06_headline li dt:not(.photo) > a");

        for (Element article : articles) {
            String title = article.text();
            String link = article.attr("href");

            // 본문 추출
            String body = extractNewsBody(link);
            if (body.length() < 100) continue;

            NewsDTO dto = new NewsDTO();
            dto.setTitle(title);
            dto.setLink(link);
            dto.setDescription(body.length() > 500 ? body.substring(0, 500) + "..." : body);
            dto.setCategory(category);
            dto.setKeyword(keyword);
            dto.setDate(date);

            results.add(dto);
        }

        return results;
    }

    private String extractNewsBody(String link) {
        try {
            Document newsDoc = Jsoup.connect(link).get();
            Element content = newsDoc.selectFirst("div#dic_area");
            return content != null ? content.text() : "";
        } catch (Exception e) {
            return "";
        }
    }
}