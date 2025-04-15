package mp.project.example.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component //spring에서 관리하는 Bean으로 등록 
//Bean은 spring이 생성하고 관리하는 객체(인스턴스)
public class JwtUtil { // JWT 토큰을 생성하고 파싱하는 유틸리티 클래스 

    @Value("${jwt.secret}") //application.properties나 환경변수에 정의된 jwt.secret 값을 가져옴 
    private String secretKey;

    private final long expireTimeMs = 1000 * 60 * 60; // 만료시간 1시간

    // JWT 생성
    //JWT는 <Header>.<Payload>.<Signature>로 구성된 문자열 
    //payload에 직접 내용을 넣는 부분이고 들어가는 내용이 claims이다. 
    //claims 정보 중 sub는 토큰이 누구의 것인지를 판별하는 식별자이다. 
    //즉, userName을 넣으면 이 토큰은 어떤 사용자에게 발급된 것이라는 뜻 
    public String createToken(String userName) {
        Claims claims = Jwts.claims().setSubject(userName);
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireTimeMs);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT에서 userName 꺼내기
    public String getUserNameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
