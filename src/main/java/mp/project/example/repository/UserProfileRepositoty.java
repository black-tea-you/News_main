package mp.project.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mp.project.example.domain.User;
import mp.project.example.domain.UserProfile;

public interface UserProfileRepositoty extends JpaRepository<UserProfile,Long>{
    Optional<UserProfile> findByUserId(Long userId);
}
