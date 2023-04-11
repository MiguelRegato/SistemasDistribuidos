package app.controller.restController;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.model.Comment;
import app.model.Film;
import app.model.User;
import app.service.CommentService;
import app.service.FilmService;
import app.service.UserService;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {

	@Autowired
	private UserService userService;

	@Autowired
	private FilmService filmService;

	@Autowired
	private CommentService commentService;
	
	@GetMapping("/{id}")
	public ResponseEntity<Comment> getComment(@PathVariable long id) {
		Optional<Comment> comment = commentService.findById(id);
		
		if (comment.isPresent()) {
			return new ResponseEntity<>(comment.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Comment> editComment(@PathVariable long id, @RequestBody Comment newComment, HttpServletRequest request) {
		Optional<Comment> optionalComment = commentService.findById(id);
		
		if (optionalComment.isPresent()) {
			Comment comment = optionalComment.get();
			Film film = comment.getFilm();
			User user = comment.getUser();
			User userPrincipal = userService.findByName(request.getUserPrincipal().getName()).orElseThrow();
			
			if (userPrincipal.getId().equals(user.getId())) {
				newComment.setId(id);
				newComment.setUser(user);
				newComment.setFilm(film);
				commentService.save(newComment);
				
				film.calculateAverage();
				filmService.save(film);
				return new ResponseEntity<>(newComment, HttpStatus.OK); 
			} else {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Comment> removeComment(@PathVariable long id, HttpServletRequest request) {
		Optional<Comment> optionalComment = commentService.findById(id);
		
		if (optionalComment.isPresent()) {
			Comment comment = optionalComment.get();
			User user = userService.findByName(request.getUserPrincipal().getName()).orElseThrow();
			User userComment = comment.getUser();
			
			if (userComment.getId().equals(user.getId()) || request.isUserInRole("ADMIN")) {
				Film film = comment.getFilm();
				commentService.delete(id);
				film.calculateAverage();
				filmService.save(film);
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}