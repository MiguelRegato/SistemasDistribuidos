package app.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import app.model.Film;
import app.model.Genre;
import app.model.Recommendation;
import app.model.User;
import app.repository.FilmRepository;

@Service
public class FilmService {
	
	@Autowired
	private FilmRepository repository;
	
	public List<Film> findAll() {
		return repository.findAll(Sort.by(Sort.Direction.DESC, "averageStars"));
	}
	
	public Page<Film> findAll(Pageable pageable) {
		Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "averageStars"));
		return repository.findAll(page);
	}

	public void save(Film film) {
		repository.save(film);
	}

	public void delete(long id) {
		repository.deleteById(id);
	}
	
	public Optional<Film> findById(long id) {
		return repository.findById(id);
	}
	
	public List<Film> findByGenre(Genre similar) {		
		return repository.findByGenre(similar, Sort.by(Sort.Direction.DESC, "averageStars"));
	}
	
	public Page<Film> findByGenre(Genre genre, Pageable pageable) {
		Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "averageStars"));
		return repository.findByGenre(genre, page);
	}
	
	public List<Film> findByGenreDistinct(Genre similar, long id) {
		return repository.findByGenreDistinct(similar, id, Sort.by(Sort.Direction.DESC, "averageStars"));
	}
	
	public Page<Film> findByGenreDistinct(Genre similar, long id, Pageable pageable) {
		Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "averageStars"));
		return repository.findByGenreDistinct(similar, id, page);
	}
	
	public Page<Film> findLikeName(String name, Pageable pageable) {
		Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "averageStars"));
		return repository.findLikeName(name, page);
	}

	public long count() {
		return repository.count();
	}

	public long countByGenre(Genre genre) {
		return repository.countByGenre(genre);
	}

	public int countByName(String name) {
		return repository.countByName(name);
	}
	
	public int countCommentsByGenre(Genre genre) {
		List<Film> films = findByGenre(genre);
		int counter = 0;
		
		for (Film film : films) {
			counter += film.getComments().size();
		}
		
		return counter;
	}
	
	public Film findFilmForRecommendation(long id, Film film, User user) {
		List<Film> films = findByGenreDistinct(film.getGenre(), id);
		List<Recommendation> recommendations = user.getRecommendations();
		HashSet<Long> filmsRecommended = new HashSet<>();
		Film recommended = null;
		
		for (int i = 0; i < recommendations.size(); i++) {
			filmsRecommended.add(recommendations.get(i).getFilm().getId());
		}
		
		if (!films.isEmpty()) {
			if (!recommendations.isEmpty()) {
				boolean equal = true;
				int i = 0;

				while ((i < films.size()) && equal) {
					Film f = films.get(i);
					
					if (!filmsRecommended.contains(f.getId())) {
						recommended = f;
						equal = false;
					}

					i++;
				}
			} else {
				recommended = films.get(0);
			}
		}
		
		return recommended;
	}
}