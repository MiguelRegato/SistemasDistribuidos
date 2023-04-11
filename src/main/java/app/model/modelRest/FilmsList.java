package app.model.modelRest;

import org.springframework.data.domain.Page;

import app.model.Film;
import app.model.Recommendation;

public class FilmsList {
	
	private Page<Recommendation> recommendations;
	private Page<Film> trending;
	private Page<Film> action;
	private Page<Film> adventure;
	private Page<Film> animation;
	private Page<Film> comedy;
	private Page<Film> drama;
	private Page<Film> horror;
	private Page<Film> scifi;
	

	public FilmsList() {

	}
	
	public FilmsList(Page<Recommendation> recommendations, Page<Film> trending, Page<Film> action, 
	Page<Film> adventure, Page<Film> animation, Page<Film> comedy, Page<Film> drama, Page<Film> horror, Page<Film> scifi) {
		this.setRecommendations(recommendations);
		this.setTrending(trending);
		this.setAction(action);
		this.setAdventure(adventure);
		this.setAnimation(animation);
		this.setComedy(comedy);
		this.setDrama(drama);
		this.setHorror(horror);
		this.setScifi(scifi);
	}

	public Page<Recommendation> getRecommendations() {
		return recommendations;
	}

	public Page<Film> getTrending() {
		return trending;
	}

	public Page<Film> getAction() {
		return action;
	}

	public Page<Film> getAdventure() {
		return adventure;
	}

	public Page<Film> getAnimation() {
		return animation;
	}

	public Page<Film> getComedy() {
		return comedy;
	}

	public Page<Film> getDrama() {
		return drama;
	}

	public Page<Film> getHorror() {
		return horror;
	}

	public Page<Film> getScifi() {
		return scifi;
	}

	private void setRecommendations(Page<Recommendation> recommendations) {
		this.recommendations = recommendations;
	}

	public void setTrending(Page<Film> trending) {
		this.trending = trending;
	}

	public void setAction(Page<Film> action) {
		this.action = action;
	}

	public void setAdventure(Page<Film> adventure) {
		this.adventure = adventure;
	}

	public void setAnimation(Page<Film> animation) {
		this.animation = animation;
	}

	public void setComedy(Page<Film> comedy) {
		this.comedy = comedy;
	}

	public void setDrama(Page<Film> drama) {
		this.drama = drama;
	}

	public void setHorror(Page<Film> horror) {
		this.horror = horror;
	}

	public void setScifi(Page<Film> scifi) {
		this.scifi = scifi;
	}
}