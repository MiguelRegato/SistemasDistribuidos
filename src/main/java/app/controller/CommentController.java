package app.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import app.model.Comment;
import app.model.Film;
import app.model.Recommendation;
import app.model.User;
import app.service.CommentService;
import app.service.FilmService;
import app.service.RecommendationService;
import app.service.SendMail;
import app.service.UserService;

@Controller
public class CommentController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FilmService filmService;
	
	@Autowired
	private RecommendationService recommendationService;
	
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
	
	@GetMapping("/addComment/{id}")
	public String addComent(Model model, @PathVariable long id, HttpServletRequest request) {
		Film film = filmService.findById(id).orElseThrow();
		User user = userService.findByName(request.getUserPrincipal().getName()).orElseThrow();
		
		if (!commentService.userHasCommented(user.getId(), film)){
			model.addAttribute("film", film);
			model.addAttribute("user", user);
			return "addComment";
		} else {
			return "redirect:/filmRegistered/" + film.getId(); 
		}
	}
	
	@PostMapping("/addComment/{id}")
	public String addComment(Model model, @PathVariable long id, Comment comment, HttpServletRequest request) {
		Film film = filmService.findById(id).orElseThrow();
		User user = userService.findByName(request.getUserPrincipal().getName()).orElseThrow();
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
		
		return "redirect:/filmRegistered/" + film.getId();
	}
	
	@GetMapping("/editComment/{id}")
	public String editComment(Model model, @PathVariable long id, HttpServletRequest request) {
		Comment comment = commentService.findById(id).orElseThrow();
		User user = userService.findByName(request.getUserPrincipal().getName()).orElseThrow();
		User userComment = comment.getUser();
	
		if (userComment.getId().equals(user.getId())) {
			model.addAttribute("comment", comment);
			model.addAttribute("user", user);
			return "editComment";
		}
		
		return "redirect:/error";
	}
	
	@PostMapping("/editComment")
	public String editComment(Model model, Comment newComment, HttpServletRequest request) throws IOException, SQLException {
		Comment comment = commentService.findById(newComment.getId()).orElseThrow();
		Film film = comment.getFilm();
		
		newComment.setUser(comment.getUser());
		newComment.setFilm(film);
		commentService.save(newComment);
		film.calculateAverage();
		filmService.save(film);
		return "redirect:/profile/" + newComment.getUser().getId();
	}
	
	@GetMapping("/removeComment/{id}")
	public String removeComment(Model model, @PathVariable long id, HttpServletRequest request) {
		Comment comment = commentService.findById(id).orElseThrow();
		User user = userService.findByName(request.getUserPrincipal().getName()).orElseThrow();
		User userComment = comment.getUser();
		
		if (userComment.getId().equals(user.getId()) || request.isUserInRole("ADMIN")) {
			Film film = comment.getFilm();
			commentService.delete(id);
			film.calculateAverage();
			filmService.save(film);
			
			if (request.isUserInRole("ADMIN")) {
				return "redirect:/filmAdmin/" + film.getId();
			} else {
				return "redirect:/profile/" + user.getId();
			}
		} 
		
		return "redirect:/error";
	}
}