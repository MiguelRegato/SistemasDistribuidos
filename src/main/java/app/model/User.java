package app.model;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table (name="Customer")
public class User{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String name;
	private String email;
	
	@OneToMany (mappedBy="user", cascade=CascadeType.ALL, orphanRemoval=true)
	@JsonIgnore
	private List<Comment> comments = new ArrayList<>();
	
	@JsonIgnore
	private String encodedPassword;

	@ElementCollection(fetch=FetchType.EAGER)
	private List<String> roles;
	
	@Lob
	@JsonIgnore
	private Blob imageFile;

	private boolean image;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JsonIgnore
	private List<User> following;
	
	private int followingCount;
	
	@ManyToMany(mappedBy="following")
	@JsonIgnore
	private List<User> followers;
	
	private int followersCount;
	
	@OneToMany (cascade=CascadeType.ALL, orphanRemoval=true)
	@JsonIgnore
	private List<Recommendation> recommendations = new ArrayList<>();
	
	public User() {
		
	}

	public User(String name, String email, String encodedPassword, String... roles) {
		this.name = name;
		this.email = email;
		this.encodedPassword = encodedPassword;
		this.roles = List.of(roles);
	}

	// Getters
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public List<Comment> getComments() {
		return comments;
	}
	
	public String getEncodedPassword() {
        return encodedPassword;
    }
	
	public List<String> getRoles() {
		return roles;
	}
	
	public Blob getImageFile() {
		return imageFile;
	}
	
	public boolean getImage() {
		return image;
	}

	public List<User> getFollowing() {
		return following;
	}
	
	public List<User> getFollowers() {
		return followers;
	}
	
	public int getFollowingCount() {
        return followingCount;
    }
	
	public int getFollowersCount() {
        return followersCount;
    }
	
	public List<Recommendation> getRecommendations() {
		return recommendations;
	}

	// Setters
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public void setEncodedPassword(String encodedPassword) {
		this.encodedPassword = encodedPassword;
	}
	
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
	public void setImageFile(Blob image) {
		this.imageFile = image;
	}
	
	public void setImage(boolean image) {
		this.image = image;
	}

	public void addComment(Comment comment) {
		comments.add(comment);
		comment.setUser(this);
	}
	
	public void deleteComment(Comment comment) {
		comments.remove(comment);
		comment.setUser(null);
	}
	
	public void addFollowing(User user) {
		following.add(user);
		user.addFollowers(this);
	}
	
	public void addFollowers(User user) {
		followers.add(user);
	}
	
	public void deleteFollowing(User user) {
		following.remove(user);
		user.deleteFollowers(this);
	}
	
	public void deleteFollowers(User user) {
		followers.remove(user);
	}
	
	public void calculateFollowing() {
    	followingCount = following.size();
    }
	
	public void calculateFollowers() {
		followersCount = followers.size();
    }
	
	public void addRecommedation(Recommendation recommendation) {
		recommendations.add(recommendation);
	}
	
	public void deleteRecommedation(Recommendation recommendation) {
		recommendations.remove(recommendation);
	}
}