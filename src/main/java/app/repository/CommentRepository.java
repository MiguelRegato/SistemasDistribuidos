package app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import app.model.Comment;
import app.model.Film;
import app.model.User;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	public long count();

	public Page<Comment> findByFilm(Film film, Pageable pageable);
	
	public List<Comment> findByFilm(Film film);

	public long countByFilm(Film film);

	public Page<Comment> findByUser(User user, Pageable pageable);
	
	public int countByUser(User user);
}