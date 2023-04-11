package app.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Comment {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private int stars;
	private String note;
	
	@ManyToOne
	private Film film;
	
	@ManyToOne
	private User user;
	
	public Comment() {
		
	}
	
	public Comment(String stars, String note) {
        this.stars = Integer.parseInt(stars);
        this.note = note;
    }

	// Getters
	public long getId() {
		return id;
	}
	
    public int getStars() {
        return stars;
    }

    public String getNote() {
        return note;
    }
    
    public Film getFilm() {
        return film;
    }
    
    public User getUser() {
    	return user;
    }

    // Setters
    public void setId(Long id) {
		this.id = id;
	}
    
    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setNote(String note) {
        this.note = note;
    }
    
	public void setFilm(Film film) {
		this.film = film;		
	}
	
	public void setUser(User user) {
		this.user = user;		
	}
}