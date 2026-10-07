package gitMind.backend.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import gitMind.backend.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByGithubId(Long githubId);
}
