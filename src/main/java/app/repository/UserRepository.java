package app.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import app.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
	
	Page<User> findByFollowersId(long id, Pageable pageable);

	Page<User> findByFollowingId(long id, Pageable pageable);
}