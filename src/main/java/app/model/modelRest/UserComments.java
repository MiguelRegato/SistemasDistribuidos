package app.model.modelRest;

import org.springframework.data.domain.Page;

import app.model.Comment;
import app.model.User;

public class UserComments {
	
	private User user;
	private Page<Comment> comments;
	
	public UserComments() {
		
	}
	
	public UserComments(User user, Page<Comment> comments) {
		this.user = user;
		this.comments = comments;
	}
	
	public User getUser() {
		return user;
	}
	
	public Page<Comment> getComments() {
		return comments;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public void setComments(Page<Comment> comments) {
		this.comments = comments;
	}
}