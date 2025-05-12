package mp.project.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import mp.project.example.security.JwtAuthenticationFilter;
import mp.project.example.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter, CustomUserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 안 씀
            .and()
            .authorizeHttpRequests()
                .requestMatchers("/api/login", "/api/register","/api/news/home","/api/news/search","/api/test/testEmbedding","/api/keywords/final","/api/news/headline").permitAll()  
                .requestMatchers("api/news/scrap").authenticated()
                .anyRequest().authenticated()
            .and()
            .formLogin().disable() // ✅ 기본 로그인 화면 제거
            .httpBasic().disable(); // ✅ 브라우저 팝업 로그인도 제거

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 가장 많이 쓰는 해시 방식
    }
}

