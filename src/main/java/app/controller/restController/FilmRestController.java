package app.controller.restController;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import app.model.Comment;
import app.model.Film;
import app.model.Genre;
import app.model.Recommendation;
import app.model.User;
import app.model.modelRest.FilmComments;
import app.model.modelRest.FilmsList;
import app.service.CommentService;
import app.service.FilmService;
import app.service.RecommendationService;
import app.service.SendMail;
import app.service.UserService;

@RestController
@RequestMapping("/api/films")
public class FilmRestController {
	
	@Autowired
	private FilmService filmService;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RecommendationService recommendationService;
	
	@GetMapping("/menu")
	public ResponseEntity<FilmsList> getMenu(HttpServletRequest request) {
    	Principal principal = request.getUserPrincipal();
    	Page<Recommendation> recommendations = null;
        
		if (principal != null && !request.isUserInRole("ADMIN")) {
			Optional<User> user = userService.findByName(request.getUserPrincipal().getName());
			
			if (user.isPresent()) {
				recommendations = recommendationService.findByUser(user.get().getId(), PageRequest.of(0, 6));
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		}
		
		Page<Film> trending = filmService.findAll(PageRequest.of(0,6));
		Page<Film> action = filmService.findByGenre(Genre.ACTION, PageRequest.of(0,6));
		Page<Film> adventure = filmService.findByGenre(Genre.ADVENTURE, PageRequest.of(0,6));
		Page<Film> animation = filmService.findByGenre(Genre.ANIMATION, PageRequest.of(0,6));
		Page<Film> comedy = filmService.findByGenre(Genre.COMEDY, PageRequest.of(0,6));
		Page<Film> drama = filmService.findByGenre(Genre.DRAMA, PageRequest.of(0,6));
		Page<Film> horror = filmService.findByGenre(Genre.HORROR, PageRequest.of(0,6));
		Page<Film> scifi = filmService.findByGenre(Genre.SCIENCE_FICTION, PageRequest.of(0,6));

		FilmsList listOfFilms = new FilmsList(recommendations, trending, action, adventure, animation, comedy, drama, horror, scifi);
		
		return new ResponseEntity<>(listOfFilms, HttpStatus.OK);
	}
	
	@GetMapping("/")
	public Page<Film> moreFilms(@RequestParam(required=false) String genre, @RequestParam(required=false) String name, @RequestParam int page) {
		if (genre != null) {
			return moreFilmsByGenre(genre, page);
		} else if (name != null) {
			return searchFilms(name, page);
		} else {
			return moreFilmsTrending(page);
		}
	}
	
	private Page<Film> moreFilmsTrending(int page) {
		// Before returning a page it confirms that there are more left
		if (page <= (int) Math.ceil(filmService.count()/6)) {
			return filmService.findAll(PageRequest.of(page,6));
		} else {
			return null;
		}
	}

	private Page<Film> moreFilmsByGenre(String genre, int page) {
		// Before returning a page it confirms that there are more left
		Genre gen = Genre.valueOf(genre);
		
		if (page <= (int) Math.ceil(filmService.countByGenre(gen)/6)) {
			return filmService.findByGenre(gen, PageRequest.of(page,6));
		} else {
			return null;	
		}
	}
	
	public Page<Film> searchFilms(String name, int page) {
		// Before returning a page it confirms that there are more left
		if (page <= (int) Math.ceil(filmService.countByName(name)/6)) {
			return filmService.findLikeName(name.toLowerCase(), PageRequest.of(page,6));
		} else {
			return null;			
		}		
	}
	
	@GetMapping("/recommendations")
	public ResponseEntity<Page<Recommendation>> moreRecommendations(@RequestParam int page, HttpServletRequest request) {
		// Before returning a page it confirms that there are more left
		Optional<User> optionalUser = userService.findByName(request.getUserPrincipal().getName());
		
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			
			if (page <= (int) Math.ceil(recommendationService.countByUser(user.getId())/6)) {
				return new ResponseEntity<>(recommendationService.findByUser(user.getId(), PageRequest.of(page,6)), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/comments/number")
	public List<Integer> calculateChart() {
		List<Integer> counters = new ArrayList<>();
		
		counters.add(filmService.countCommentsByGenre(Genre.ACTION));
		counters.add(filmService.countCommentsByGenre(Genre.ADVENTURE));
		counters.add(filmService.countCommentsByGenre(Genre.ANIMATION));
		counters.add(filmService.countCommentsByGenre(Genre.COMEDY));
		counters.add(filmService.countCommentsByGenre(Genre.DRAMA));
		counters.add(filmService.countCommentsByGenre(Genre.HORROR));
		counters.add(filmService.countCommentsByGenre(Genre.SCIENCE_FICTION));
		
		return counters;
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<FilmComments> getFilm(@PathVariable long id) {
		Optional<Film> optionalFilm = filmService.findById(id);
		
		if (optionalFilm.isPresent()) {
			Film film = optionalFilm.get();
			
			Genre similar = film.getGenre();
			Page<Film> similarList = filmService.findByGenreDistinct(similar, film.getId(), PageRequest.of(0,6));
			
			film.setSimilar(similarList.toList());
			Page<Comment> comments = commentService.findByFilm(film, PageRequest.of(0,2));
			FilmComments filmComments = new FilmComments(film, comments);
			return new ResponseEntity<>(filmComments, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
		Optional<Film> film = filmService.findById(id);
		
		if (film.isPresent() && film.get().getImageFile() != null) {
			Resource file = new InputStreamResource(film.get().getImageFile().getBinaryStream());
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").contentLength(film.get().getImageFile().length()).body(file);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/{id}/comments")
	public ResponseEntity<Page<Comment>> moreComments(@PathVariable long id, @RequestParam int page) {
		// Before returning a page it confirms that there are more left
		Optional<Film> optionalFilm = filmService.findById(id);
		
		if (optionalFilm.isPresent()) {
			Film film = optionalFilm.get();
			
			if (page <= (int) Math.ceil(commentService.countByFilm(film)/2)) {
				return new ResponseEntity<>(commentService.findByFilm(film, PageRequest.of(page, 2)), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/")
	@ResponseStatus(HttpStatus.CREATED)
	public Film addFilm(@RequestBody Film film) {					
		filmService.save(film);		
		return film;
	}
	
	@PostMapping("/{id}/image")
	public ResponseEntity<Object> uploadImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException{
		Optional<Film> optionalFilm = filmService.findById(id);
		
		if (optionalFilm.isPresent()) {
			Film film = optionalFilm.get();
			URI location = fromCurrentRequest().build().toUri();
			
			film.setImage(true);
			film.setImageFile(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
			
			filmService.save(film);
			return ResponseEntity.created(location).build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PutMapping("/{id}/image")
	public ResponseEntity<Object> editImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException{
		Optional<Film> optionalFilm = filmService.findById(id);
		
		if (optionalFilm.isPresent()) {
			Film film = optionalFilm.get();
			film.setImageFile(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
			filmService.save(film);
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Film> editFilm(@RequestBody Film newFilm, @PathVariable long id) {
		Optional<Film> optionalFilm = filmService.findById(id);

	    if (optionalFilm.isPresent()) {
	    	Film film = optionalFilm.get();
	    	film.setTitle(newFilm.getTitle());
			film.setReleaseDate(newFilm.getReleaseDate());
			film.setCast(newFilm.getCast());
			film.setDuration(newFilm.getDuration());
			film.setMinAge(newFilm.getMinAge());
			film.setGenre(newFilm.getGenre());
			film.setDirector(newFilm.getDirector());
			film.setPlot(newFilm.getPlot());
			
			filmService.save(film);
	        return new ResponseEntity<>(film, HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Film> removeFilm(@PathVariable long id) {
		Optional<Film> film = filmService.findById(id);
			
		if (film.isPresent()) {
			filmService.delete(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/{id}/comments")
	public ResponseEntity<Comment> addComment(@PathVariable long id, HttpServletRequest request, @RequestBody Comment comment) {
		Optional<Film> optionalFilm = filmService.findById(id);
		
		if (optionalFilm.isPresent()) {
			Film film = optionalFilm.get();
			User user = userService.findByName(request.getUserPrincipal().getName()).orElseThrow();
			
			if (!commentService.userHasCommented(user.getId(), film)) {
				comment.setFilm(film);
				comment.setUser(user);
				commentService.save(comment);
				
				film.calculateAverage();
				filmService.save(film);
				
				Film recommended = filmService.findFilmForRecommendation(id, film, user);
				
				if (recommended != null) {
					Recommendation recommendation = new Recommendation(recommended);
					recommendationService.save(recommendation);
					user.addRecommedation(recommendation);
					userService.save(user);
				
					SendMail.sendMail(film, user);
				}
				
				return new ResponseEntity<>(comment, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}