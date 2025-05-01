package mp.project.example.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import mp.project.example.domain.News;
import mp.project.example.domain.User;
import mp.project.example.dto.NewsDTO;
import mp.project.example.repository.NewsRepository;
import mp.project.example.repository.UserRepository;

@Service
public class NewsCrawlingService {
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final OpenAIService openAIService;

    public NewsCrawlingService(NewsRepository newsRepository, UserRepository userRepository, OpenAIService openAIService) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
        this.openAIService = openAIService;
    }

    public List<NewsDTO> crawlCategoryAndDate(String lstcode, String startDateStr, String endDateStr) throws Exception {
        System.out.println(" 크롤링 시작: lstcode=" + lstcode + ", start=" + startDateStr + ", end=" + endDateStr);

        List<NewsDTO> results = new ArrayList<>();
        //SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        SimpleDateFormat compareFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date startDate = compareFormat.parse(startDateStr);
        Date endDate = compareFormat.parse(endDateStr);
        int page = 1;
        int count = 0;
        int oldDateCount=0;
        boolean stop = false;
        while(!stop){
        String categoryUrl = "https://zdnet.co.kr/news/?lstcode=" + lstcode + "&page=" + page;
        Document doc = Jsoup.connect(categoryUrl)
                .userAgent("Mozilla/5.0")
                .get();

        Elements newsItems = doc.select("div.newsPost");
        if (newsItems.isEmpty()) break;
        System.out.println(" 크롤링 URL: " + categoryUrl);
        System.out.println(" 뉴스 개수: " + newsItems.size());
        for (Element newsItem : newsItems) {
            Element linkElement = newsItem.selectFirst("a[href]");
            if (linkElement == null) {
                System.out.println(" 링크 없음, 건너뜀");
                continue;}

            String link = linkElement.absUrl("href");
            Element titleElement = newsItem.selectFirst("div.assetText h3");
            String title = titleElement != null ? titleElement.text() : "(제목 없음)";
            

            //  썸네일 추출 (assetThumb 기준)
            Element imgEl = newsItem.selectFirst("div.assetThumb img");
            String urlimg = imgEl != null ? imgEl.absUrl("src") : null;

            Document articleDoc = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0")
                    .get();
             //System.out.println("🧾 원본 HTML 샘플:");
               // System.out.println(articleDoc.outerHtml().substring(0, 2000)); // 2000자만 출력

            
          String dateFromUrl = link.split("no=")[1];
            String articleDateStr = dateFromUrl.substring(0, 4) + "-" +
                        dateFromUrl.substring(4, 6) + "-" +
                        dateFromUrl.substring(6, 8);
            Date articleDate = compareFormat.parse(articleDateStr);
            System.out.println(" URL에서 추출한 날짜: " + articleDateStr);
            System.out.println(" 기사 날짜: " + articleDateStr);
            System.out.println(" 기준 시작: " + compareFormat.format(startDate));
            System.out.println(" 기준 끝: " + compareFormat.format(endDate));
            
            if (articleDate.before(startDate)) {
                oldDateCount++;

                System.out.println(" 날짜 이전 기사 (연속 "+oldDateCount+"개)");
                if(oldDateCount>=5){
                    System.out.println("날짜 이전 기사 연속 5개: 크롤링 종료");
                    stop = true; 
                     break;
                }
                continue; //날짜 이전 기사면 건너뜀 

            }else{
                oldDateCount=0; //날짜 포함 기사면 카운트 초기화 
            }
            if (articleDate.after(endDate)){
                System.out.println(" 날짜 초과 기사. 다음 기사로");
                 continue;
            }

            Element content = articleDoc.selectFirst("div#articleBody");
            if (content == null || content.text().length() < 10){
                System.out.println(" 본문 없음 또는 너무 짧음, 건너뜀 → 링크: " + link);
    continue;
            } 

            String body = content.text();
            //String summary = openAIService.summarizeText(body);
            String summary="(요약 생략)";

            News news = new News();
            news.setTitle(title);
            news.setDescription(body.length() > 1000 ? body.substring(0, 1000) + "..." : body);
            news.setSummary(summary);
            news.setLink(link);
            news.setDate(articleDateStr);
            //news.setCategory("ZDNet");
            news.setUrlimg(urlimg);

           /**  String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUserName(userName)
                    .orElseThrow(() -> new RuntimeException("User not found"));**/
             User user = userRepository.findByUserName("abcd") //테스트 용으로 고정된 이름 사용 
                    .orElseThrow(() -> new RuntimeException("Test user not found"));
            news.setUser(user);
           
            newsRepository.save(news);

            String categoryName;
            switch (lstcode) {
                case "0000": categoryName = "전체"; break;
                case "0010": categoryName = "방송/통신"; break;
                case "0020": categoryName = "컴퓨팅"; break;
                case "0030": categoryName = "홈&모바일"; break;
                case "0040": categoryName = "인터넷"; break;
                case "0050": categoryName = "반도체/디스플레이"; break;
                case "0060": categoryName = "게임"; break;
                case "0070": categoryName = "과학"; break;
                default: categoryName = lstcode; break;
            }
            NewsDTO dto = new NewsDTO();
            dto.setCategory(categoryName);
            dto.setTitle(title);
            dto.setLink(link);
            dto.setDescription(body.length() > 500 ? body.substring(0, 500) + "..." : body);
            dto.setDate(articleDateStr);
            dto.setSummary(summary);
            dto.setKeyword("");
            dto.setUrlimg(urlimg);

            results.add(dto);
            count++;

            if (count >= 5){ stop=true; break;}
        }
        page++;
    }
        return results;
    }
}
