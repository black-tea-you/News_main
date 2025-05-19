package mp.project.example.dto;

/**
 * 로그인 성공 시 클라이언트로 보낼 JSON 형태를 정의합니다.
 */
public class LoginResponse {
    private String accessToken;

    // 기본 생성자 (Jackson용)
    public LoginResponse() { }

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}