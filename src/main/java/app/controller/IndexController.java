package app.controller;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import app.model.Genre;
import app.model.User;
import app.service.FilmService;
import app.service.RecommendationService;
import app.service.UserService;

@Controller
public class IndexController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private FilmService filmService;
	
	@Autowired
	private RecommendationService recommendationService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
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
	
	@GetMapping("/")
	public String adviceMe(Model model) {	
		model.addAttribute("trending", filmService.findAll(PageRequest.of(0,6)));
		model.addAttribute("action", filmService.findByGenre(Genre.ACTION, PageRequest.of(0,6)));
		model.addAttribute("adventure", filmService.findByGenre(Genre.ADVENTURE, PageRequest.of(0,6)));
		model.addAttribute("animation", filmService.findByGenre(Genre.ANIMATION, PageRequest.of(0,6)));
		model.addAttribute("comedy", filmService.findByGenre(Genre.COMEDY, PageRequest.of(0,6)));
		model.addAttribute("drama", filmService.findByGenre(Genre.DRAMA, PageRequest.of(0,6)));
		model.addAttribute("horror", filmService.findByGenre(Genre.HORROR, PageRequest.of(0,6)));
		model.addAttribute("scifi", filmService.findByGenre(Genre.SCIENCE_FICTION, PageRequest.of(0,6)));

		return "adviceMe";
	}
	
    @RequestMapping("/login")
	public String login() {
		return "login";
	}
	
	@RequestMapping("/loginerror")
	public String loginerror() {
		return "loginerror";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}
	
	@PostMapping("/registerProcess")
	public String registerProcess(Model model, User user) throws IOException {
		if (!userService.existName(user.getName())) {
			Resource image = new ClassPathResource("/static/Images/defaultImage.png");
			user.setImageFile(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
			user.setImage(true);
			user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
			userService.save(user);
			return "redirect:/login";
		} else {
			return "redirect:/takenUserName";
		}
	}
	
	@GetMapping("/takenUserName")
	public String takenUserName(Model model, HttpServletRequest request) {
		return "takenUserName";
	}
	
	@GetMapping("/menuRegistered")
	public String menuRegistered(Model model, HttpServletRequest request) {
    	User user = userService.findByName(request.getUserPrincipal().getName()).orElseThrow();
    	
		if (request.isUserInRole("ADMIN")) {
			return "redirect:/menuAdmin";
		} else {
			model.addAttribute("recommendations", recommendationService.findByUser(user.getId(), PageRequest.of(0,6)));
			model.addAttribute("trending", filmService.findAll(PageRequest.of(0,6)));
			model.addAttribute("action", filmService.findByGenre(Genre.ACTION, PageRequest.of(0,6)));
			model.addAttribute("adventure", filmService.findByGenre(Genre.ADVENTURE, PageRequest.of(0,6)));
			model.addAttribute("animation", filmService.findByGenre(Genre.ANIMATION, PageRequest.of(0,6)));
			model.addAttribute("comedy", filmService.findByGenre(Genre.COMEDY, PageRequest.of(0,6)));
			model.addAttribute("drama", filmService.findByGenre(Genre.DRAMA, PageRequest.of(0,6)));
			model.addAttribute("horror", filmService.findByGenre(Genre.HORROR, PageRequest.of(0,6)));
			model.addAttribute("scifi", filmService.findByGenre(Genre.SCIENCE_FICTION, PageRequest.of(0,6)));

			model.addAttribute("user", user);
			return "menuRegistered";
		}
	}

	@GetMapping("/menuAdmin")
	public String menuAdmin(Model model, HttpServletRequest request) {
		model.addAttribute("trending", filmService.findAll(PageRequest.of(0,6)));
		model.addAttribute("action", filmService.findByGenre(Genre.ACTION, PageRequest.of(0,6)));
		model.addAttribute("adventure", filmService.findByGenre(Genre.ADVENTURE, PageRequest.of(0,6)));
		model.addAttribute("animation", filmService.findByGenre(Genre.ANIMATION, PageRequest.of(0,6)));
		model.addAttribute("comedy", filmService.findByGenre(Genre.COMEDY, PageRequest.of(0,6)));
		model.addAttribute("drama", filmService.findByGenre(Genre.DRAMA, PageRequest.of(0,6)));
		model.addAttribute("horror", filmService.findByGenre(Genre.HORROR, PageRequest.of(0,6)));
		model.addAttribute("scifi", filmService.findByGenre(Genre.SCIENCE_FICTION, PageRequest.of(0,6)));
		
		model.addAttribute("user", userService.findByName(request.getUserPrincipal().getName()).orElseThrow());
		return "menuAdmin";
	}
}