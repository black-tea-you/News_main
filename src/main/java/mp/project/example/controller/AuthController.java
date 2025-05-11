package mp.project.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mp.project.example.domain.User;
import mp.project.example.domain.UserProfile;
import mp.project.example.dto.LoginRequest;
import mp.project.example.dto.RegisterRequest;
import mp.project.example.repository.UserProfileRepositoty;
import mp.project.example.repository.UserRepository;
import mp.project.example.util.JwtUtil;

@RestController  //이 클래스가 REST API의 요청을 처리한다는 의미 
@RequestMapping("/api")  // 이 컨트롤러의 모든 URL은 /api로 시작 
public class AuthController {
   
    private final UserRepository userRepository; //DB에서 사용자 조회/저장 
    private final PasswordEncoder passwordEncoder; // 비밀버호 암호화 및 비교 
    private final JwtUtil jwtUtil; //로그인 성공 시 JWT 토큰 생성용 유틸 클래스
    private final UserProfileRepositoty userProfileRepositoty;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,UserProfileRepositoty userProfileRepositoty) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userProfileRepositoty=userProfileRepositoty;
    }// 생성자 주입 방식으로 의존성 주입(spring이 자동으로 객체 생성해서 넣어줌)
    //의존한다라는 것은 어떤 클래스가 다른 클래스의 기능 없이는 제대로 동작할 수 없는것 
    //AuthController는 UserRepository를 사용, 즉, UserRepository없이는 동작 불가
    //AuthController는 UserRepository에 의존한다. 

    // 회원가입
    @PostMapping("/register") //Post /api/register로 요청이오면 실행 
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) { //dto사용
        if (userRepository.existsByUserName(request.userName)) {
            return ResponseEntity.badRequest().body("이미 존재하는 아이디입니다.");
        } // 중복 아이디 검사 

        User user = new User();
        user.setUserName(request.userName);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setEmail(request.email);
        userRepository.save(user); // DB 저장 Spring Data JPA가 자동으로 제공
        // JpaRepository를 상속하면 save(),findAll(),findById() 같은 메서드가 추가됨

        // 회원가입 시 프로필 생성
        userProfileRepositoty.save(new UserProfile(user));

        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인
    @PostMapping("/login") //POST /api/login으로 요청이 오면 실행 
    public ResponseEntity<String> login(@RequestBody LoginRequest request) { //LoginReques dto 사용
        User user = userRepository.findByUserName(request.userName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(request.password, user.getPassword())) {
            return ResponseEntity.status(401).body("비밀번호가 일치하지 않습니다.");
        }

         // 로그인 시 프로필 체크/생성
        userProfileRepositoty.findByUserId(user.getId())
        .orElseGet(() -> userProfileRepositoty.save(new UserProfile(user)));

        String token = jwtUtil.createToken(user.getUserName());
        return ResponseEntity.ok(token); // 클라이언트는 이 토큰을 저장해서 이후 요청에 사용
    }
}

