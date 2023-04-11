package app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import app.model.Recommendation;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
	
	@Query("SELECT u.recommendations FROM User u where u.id = :id")
	public List<Recommendation> findRecommendationsByUser(long id);
	
	@Query("SELECT u.recommendations FROM User u where u.id = :id")
	public Page<Recommendation> findRecommendationsByUser(long id, Pageable pageable);
}