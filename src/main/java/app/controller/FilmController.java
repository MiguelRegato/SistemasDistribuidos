package app.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import app.model.Film;
import app.model.Genre;
import app.model.User;
import app.service.CommentService;
import app.service.FilmService;
import app.service.UserService;

@Controller
public class FilmController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FilmService filmService;
	
	@Autowired
	private CommentService commentService;
	
	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();

		if (principal != null) {
			model.addAttribute("logged", true);
			model.addAttribute("admin", request.isUserInRole("ADMIN"));
		} else {
			model.addAttribute("logged", false);
		}
	}
	
	@GetMapping("/filmUnregistered/{id}")
	public String filmUnregistered(Model model, @PathVariable long id) {
		Film film = filmService.findById(id).orElseThrow();
		model.addAttribute("film", film);
		model.addAttribute("comments", commentService.findByFilm(film, PageRequest.of(0,2)));
		Genre similar = film.getGenre();
		model.addAttribute("similar", filmService.findByGenreDistinct(similar, film.getId(), PageRequest.of(0,6)));
		return "filmUnregistered";
	} 
	
	@GetMapping("/filmRegistered/{id}")
	public String filmRegistered(Model model, @PathVariable long id, HttpServletRequest request) {
		Film film = filmService.findById(id).orElseThrow();
		model.addAttribute("film", film);
		User user = userService.findByName(request.getUserPrincipal().getName()).orElseThrow();
		model.addAttribute("user", user);
		model.addAttribute("comments", commentService.findByFilm(film, PageRequest.of(0,2)));
		Genre similar = film.getGenre();
		model.addAttribute("similar", filmService.findByGenreDistinct(similar, film.getId(), PageRequest.of(0,6)));
		model.addAttribute("buttonUnhidden", !commentService.userHasCommented(user.getId(), film));
		return "filmRegistered";
	}
	
	@GetMapping("/filmAdmin/{id}")
	public String filmAdmin(Model model, @PathVariable long id, HttpServletRequest request) {
		Film film = filmService.findById(id).orElseThrow();
		User user = userService.findByName(request.getUserPrincipal().getName()).orElseThrow();
		model.addAttribute("film", film);
		model.addAttribute("comments", commentService.findByFilm(film, PageRequest.of(0,2)));
		model.addAttribute("user", user);
		Genre similar = film.getGenre();
		model.addAttribute("similar", filmService.findByGenreDistinct(similar, film.getId(), PageRequest.of(0,6)));
		return "filmAdmin";
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
	
	@GetMapping("/searchFilms")
	public String searchFilms(Model model, @RequestParam String query, HttpServletRequest request) {
		Page<Film> result = filmService.findLikeName(query.toLowerCase(), PageRequest.of(0,6));
		model.addAttribute("result", result);
		Principal principal = request.getUserPrincipal();

		if (principal != null) {
			model.addAttribute("user", userService.findByName(principal.getName()).orElseThrow());
		}
		
		if (!result.isEmpty()) {
			model.addAttribute("exist", true);
		}
		
		return "searchFilms";
	}
	
	@GetMapping("/addFilm")
	public String addFilm(Model model, HttpServletRequest request) {
		User user = userService.findByName(request.getUserPrincipal().getName()).orElseThrow();
		model.addAttribute("user", user);
		return "addFilm";
	}
	
	@PostMapping("/addFilm")
	public String addFilmProcess(Model model, Film film, MultipartFile imageField) throws IOException {			
		if (!imageField.isEmpty()) {
			film.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
			film.setImage(true);
		}
		
		filmService.save(film);		
		return "redirect:/menuAdmin";
	}
	
	@GetMapping("/editFilm/{id}")
	public String editFilm(Model model, @PathVariable long id, HttpServletRequest request) {
		Optional<Film> film = filmService.findById(id);
		
		if (film.isPresent()) {
			User user = userService.findByName(request.getUserPrincipal().getName()).orElseThrow();
			model.addAttribute("user", user);
			model.addAttribute("film", film.get());
			return "editFilmPage";
		}
		
		return "redirect:/menuAdmin";
	}
	
	@PostMapping("/editFilm/{id}")
	public String editFilmProcess(Model model, Film newFilm, @PathVariable long id, MultipartFile imageField) throws IOException, SQLException {
		Film film = filmService.findById(id).orElseThrow();
		updateImage(film, imageField);
		
		film.setTitle(newFilm.getTitle());
		film.setReleaseDate(newFilm.getReleaseDate());
		film.setCast(newFilm.getCast());
		film.setDuration(newFilm.getDuration());
		film.setMinAge(newFilm.getMinAge());
		film.setGenre(newFilm.getGenre());
		film.setDirector(newFilm.getDirector());
		film.setPlot(newFilm.getPlot());
		
		filmService.save(film);
		return "redirect:/filmAdmin/" + film.getId();
	}
	
	@GetMapping("/removeFilm/{id}")
	public String removeFilm(Model model, @PathVariable long id) {
		Optional<Film> film = filmService.findById(id);
		
		if (film.isPresent()) {
			filmService.delete(id);
		}
		
		return "redirect:/menuAdmin";
	}
	
	private void updateImage(Film film, MultipartFile imageField) throws IOException, SQLException {
		if (!imageField.isEmpty()) {
			film.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
			film.setImage(true);
		}
	}
}