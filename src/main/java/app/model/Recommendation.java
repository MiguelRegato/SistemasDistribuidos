package app.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Recommendation {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@OneToOne
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Film filmRecommended;
	
	public Recommendation() {
		
	}
	
	public Recommendation(Film filmRecommended) {
		this.filmRecommended = filmRecommended;
	}
	
	// Getters
    public Long getId() {
    	return id;
    }
    
    public Film getFilm() {
    	return filmRecommended;
    }
    
    // Setters
 	public void setId(Long id) {
 		this.id = id;
 	}
 	
 	public void setFilm(Film filmRecommended) {
 		this.filmRecommended = filmRecommended;
 	}
}