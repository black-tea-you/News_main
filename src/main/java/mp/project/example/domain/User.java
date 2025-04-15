package mp.project.example.domain;

import java.security.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // 이 클래스가 JPA에서 관리할 엔티티 객체임을 나타냄 
@Table(name = "user") // 이 엔티티가 DB의 user 테이블과 매핑됨을 나타냄 
@Getter @Setter @NoArgsConstructor //@Getter: getter 메소드 생성 , @setter: setter 메소드 생성
//@NoArgsConstructor: 기본 생성자 생성  ,lombok 라이브러리 사용
//Lombok은 getter, setter, toString, equals, hashcode 메소드 등을 자동으로 생성해주는 라이브러리 
public class User {
    @Id//primary key임을 의미함
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto increment 의미미
    private Long id;

    private String email;

    @Column(name = "user_name",nullable=false,unique=true)// 로그인 ID로 사용할거기 떄문에 unique=true
    private String userName;   // java 필드명은 userName, DB 컬럼은 user_name

    @Column(nullable=false)
    private String password;

    @Column(name = "create_time")
    private Timestamp createTime; //필드명은 createTime, DB 컬럼은 create_time
    //Timestamp는 시간,날짜를 저장하는 타입 
}

//JPA는 필드명과 컬럼명이 같으면 @Column 어노테이션 생략 가능 
//user_name과 create_time의 필드명을 바꾼 이유는java에서 자주쓰는 camelcase로 작성하기 위함이다. 
