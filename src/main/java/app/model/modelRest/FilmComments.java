package app.model.modelRest;

import org.springframework.data.domain.Page;

import app.model.Comment;
import app.model.Film;

public class FilmComments {
	
	private Film film;
	private Page<Comment> comments;
	
	public FilmComments() {
		
	}
	
	public FilmComments(Film film, Page<Comment> comments) {
		this.film = film;
		this.comments = comments;
	}
	
	public Film getFilm() {
		return film;
	}
	
	public Page<Comment> getComments() {
		return comments;
	}
	
	public void setFilm(Film film) {
		this.film = film;
	}
	
	public void setComments(Page<Comment> comments) {
		this.comments = comments;
	}
}