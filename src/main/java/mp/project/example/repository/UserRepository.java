package mp.project.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mp.project.example.domain.User;

public interface UserRepository extends JpaRepository<User,Long>{
    //User : UserRepository가 다룰 엔티티 클래스 
    //Long : User 엔티티의 primary key 타입 
    Optional<User> findByUserName(String userName); // 로그인 시 유저이름으로 사용자 조회 
    boolean existsByUserName(String userName); // 회원가입 시 중복 확인용
    
}
//spring에서는 Repository 인터페이스를 선언하면 springboot_jpa가 자동으로
// DB연결, SQL 실행, 트랜잭션 처리까지 다 해줌 
//spring boot는 application.properties 설정을 보고 DB에 연결해줌 
