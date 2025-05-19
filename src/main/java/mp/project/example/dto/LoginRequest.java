package mp.project.example.dto;
//DTO는 API 요청이나 응답을 위해 만들어진 데이터 클래스 
//로그인 요청을 받을 때 
public class LoginRequest {
    public String userName;
    public String password;

    public LoginRequest() { }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
