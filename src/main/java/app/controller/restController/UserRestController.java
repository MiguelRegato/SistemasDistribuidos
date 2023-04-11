package app.controller.restController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import app.model.Comment;
import app.model.User;
import app.model.modelRest.UserComments;
import app.service.CommentService;
import app.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@GetMapping("/{id}")
	public ResponseEntity<UserComments> getProfile(@PathVariable long id) {
		Optional<User> userPrincipal = userService.findById(id);
		
		if (userPrincipal.isPresent()) {
			User user = userPrincipal.get();
			Page<Comment> comments = commentService.findByUser(user, PageRequest.of(0,5));
			UserComments userComments = new UserComments(user, comments);
			return new ResponseEntity<>(userComments, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/me")
	public ResponseEntity<UserComments> me(HttpServletRequest request) {
		Optional<User> userPrincipal = userService.findByName(request.getUserPrincipal().getName());
		
		if (userPrincipal.isPresent()) {
			User user = userPrincipal.get();
			Page<Comment> comments = commentService.findByUser(user, PageRequest.of(0,5));
			UserComments userComments = new UserComments(user, comments);
			return new ResponseEntity<>(userComments, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
		Optional<User> user = userService.findById(id);
		
	    if (user.isPresent() && user.get().getImageFile() != null) {
	        Resource file = new InputStreamResource(user.get().getImageFile().getBinaryStream());
	        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").contentLength(user.get().getImageFile().length()).body(file);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
	@GetMapping("/{id}/comments")
	public ResponseEntity<Page<Comment>> moreComments(@PathVariable long id, @RequestParam int page) {
	    // Before returning a page it confirms that there are more left
	    Optional<User> optionalUser = userService.findById(id);

	    if (optionalUser.isPresent()) {
	    	User user = optionalUser.get();
	    	
		    if (page <= (int) Math.ceil(commentService.countByUser(user)/5)) {
		    	Page<Comment> comments =commentService.findByUser(user, PageRequest.of(page, 5));
		        return new ResponseEntity<>(comments, HttpStatus.OK);
		    } else {
		    	return new ResponseEntity<>(HttpStatus.NOT_FOUND);           
		    }
	    } else {
	    	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
	
	@PostMapping("/")
	public ResponseEntity<User> register(@RequestParam String name, @RequestParam String email, @RequestParam String password) throws IOException {
		if (!userService.existName(name)) {
			User user = new User();
			Resource image = new ClassPathResource("/static/Images/defaultImage.png");
			user.setImageFile(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
			user.setImage(true);
			user.setName(name);
			user.setEmail(email);
			user.setEncodedPassword(passwordEncoder.encode(password));
			user.setRoles(Arrays.asList("USER"));
			userService.save(user);
			return new ResponseEntity<>(user, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN); //takenUserName
		}
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<User> editProfile(@PathVariable long id, @RequestParam String newEmail, HttpServletRequest request) throws IOException, SQLException {
		Optional<User> optionalUser = userService.findById(id);
		
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			User userPrincipal = userService.findByName(request.getUserPrincipal().getName()).orElseThrow();
			
			if (userPrincipal.getId().equals(user.getId())) {
				user.setEmail(newEmail);
				userService.save(user);
				return new ResponseEntity<>(user, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PutMapping("/{id}/image")
	public ResponseEntity<Object> editImage(@PathVariable long id, @RequestParam MultipartFile imageFile, HttpServletRequest request) throws IOException {
		Optional<User> optionalUser = userService.findById(id);
		
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			User userPrincipal = userService.findByName(request.getUserPrincipal().getName()).orElseThrow();
			
			if (userPrincipal.getId().equals(user.getId())) {
				user.setImageFile(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
				userService.save(user);
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}	
	}
	
	@PatchMapping("/{id}/password")
	public ResponseEntity<User> editPassword(@PathVariable long id, @RequestParam String oldPassword, @RequestParam String newPassword, HttpServletRequest request) throws IOException, SQLException {
		Optional<User> optionalUser = userService.findById(id);
		
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			User userPrincipal = userService.findByName(request.getUserPrincipal().getName()).orElseThrow();
			
			if (userPrincipal.getId().equals(user.getId())) {
				if (passwordEncoder.matches(oldPassword, user.getEncodedPassword())){
					user.setEncodedPassword(passwordEncoder.encode(newPassword));
					userService.save(user);
					return new ResponseEntity<>(user, HttpStatus.OK);
				} else {
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}
			} else {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/{id}/followers")
	public ResponseEntity<Page<User>> followers(@PathVariable long id, @RequestParam int page) {
		Optional<User> user = userService.findById(id);
		
		if (user.isPresent() && page <= (int) Math.ceil(user.get().getFollowersCount()/5)) {
			Page<User> followers = userService.findFollowingById(id, PageRequest.of(page, 5));
			return new ResponseEntity<>(followers, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/{id}/following")
	public ResponseEntity<Page<User>> following(@PathVariable long id, @RequestParam int page) {
		Optional<User> user = userService.findById(id);
		
		if (user.isPresent() && page <= (int) Math.ceil(user.get().getFollowingCount()/5)) {
			Page<User> following = userService.findFollowersById(id, PageRequest.of(page, 5));
			return new ResponseEntity<>(following, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/{id}/followed")
	public ResponseEntity<User> followUnfollow(@PathVariable long id, HttpServletRequest request) {
		User follower = userService.findByName(request.getUserPrincipal().getName()).orElseThrow();
		Optional<User> optionalFollowing = userService.findById(id);
		
		if (optionalFollowing.isPresent()) {
			User following = optionalFollowing.get();
			
			if (!follower.getFollowing().contains(following)) {
				follower.addFollowing(following);
			} else {
				follower.deleteFollowing(following);
			}
			
			follower.calculateFollowers();
			follower.calculateFollowing();
					
			following.calculateFollowers();
			following.calculateFollowing();
			
			userService.save(follower);
			return new ResponseEntity<>(following, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}