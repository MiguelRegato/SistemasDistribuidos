package app.model;

import java.sql.Blob;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Film {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String title;
	private Date releaseDate;
	private float averageStars;
	private String minAge;
	private Genre genre;
	private int duration;
	
	@Column (name="casting")
	private String cast;
	
	private String director;
	private String plot;
	
	@OneToMany (mappedBy="film", cascade=CascadeType.ALL, orphanRemoval=true)
	@JsonIgnore
	private List<Comment> comments = new ArrayList<>();

	@Lob
	@JsonIgnore
	private Blob imageFile;
	
	private boolean image;
 
	@Transient
	private List<Film> similar;
	
	public Film() {
		
	}
	
    public Film(String title, Date releaseDate, String minAge, String genre, int duration, String cast, String director, String plot) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.minAge = minAge;
        this.genre = Genre.valueOf(genre);
        this.duration = duration;
        this.cast = cast;
        this.director = director;
        this.plot = plot;
    }
	
    public void calculateAverage() {
    	if (comments.size() != 0) {
    		float sum = 0;
        	
        	for (Comment comment: comments) {
        		sum += comment.getStars();
        	}
        	
        	averageStars = sum / comments.size();
    	}
    	else { 
    		averageStars = 0;
    	}
    }
    
	// Getters
    public Long getId() {
    	return id;
    }
    
	public String getTitle() {
        return title;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }
    
    public float getAverageStars() {
    	calculateAverage();
    	return averageStars;
    }

    public String getMinAge() {
        return minAge;
    }

    public Genre getGenre() {
        return genre;
    }

    public int getDuration() {
        return duration;
    }

    public String getCast() {
        return cast;
    }

    public String getDirector() {
        return director;
    }

    public String getPlot() {
        return plot;
    }
    
	public Blob getImageFile() {
		return imageFile;
	}
	
	public boolean getImage(){
		return image;
	}
	
	public List<Comment> getComments() {
		return comments;
	}
	
	public List<Film> getSimilar() {
		return similar;	
	}
	
    // Setters
	public void setId(Long id) {
		this.id = id;
	}
	
    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setMinAge(String minAge) {
        this.minAge = minAge;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

	public void setImageFile(Blob image) {
		this.imageFile = image;
	}
	
	public void setImage(boolean image){
		this.image = image;
	}
	
	public void addComment(Comment comment) {
		comments.add(comment);
		comment.setFilm(this);
		calculateAverage();
	}
	
	public void deleteComment(Comment comment) {
		comments.remove(comment);
		comment.setFilm(null);
		calculateAverage();
	}
	
	public void setSimilar(List<Film> similar) {
		this.similar = similar;	
	}
}