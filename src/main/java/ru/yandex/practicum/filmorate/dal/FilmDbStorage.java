package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private static final String SELECT_FILM = """
            SELECT f.film_id, f.name, f.description, f.release_date, f.duration,
                   f.mpa_rating_id, mr.name AS mpa_name
            FROM films f
            LEFT JOIN mpa_rating mr ON f.mpa_rating_id = mr.rating_id
            """;

    private static final String FIND_BY_ID = SELECT_FILM + " WHERE f.film_id = ?";
    private static final String FIND_ALL = SELECT_FILM + " ORDER BY f.film_id";

    private static final String FIND_POPULAR = """
            SELECT f.film_id, f.name, f.description, f.release_date, f.duration,
                   f.mpa_rating_id, mr.name AS mpa_name,
                   COUNT(l.user_id) AS likes_count
            FROM films f
            LEFT JOIN mpa_rating mr ON f.mpa_rating_id = mr.rating_id
            LEFT JOIN likes l ON f.film_id = l.film_id
            GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, 
                     f.mpa_rating_id, mr.name
            ORDER BY likes_count DESC, f.film_id
            LIMIT ?
            """;

    private static final String INSERT = """
            INSERT INTO films (name, description, release_date, duration, mpa_rating_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE = """
            UPDATE films SET name = ?, description = ?, release_date = ?, 
            duration = ?, mpa_rating_id = ? WHERE film_id = ?
            """;

    private static final String DELETE = "DELETE FROM films WHERE film_id = ?";
    private static final String EXISTS = "SELECT EXISTS(SELECT 1 FROM films WHERE film_id = ?)";
    private static final String ADD_LIKE = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper filmRowMapper) {
        super(jdbcTemplate, filmRowMapper);
    }

    @Override
    public Collection<Film> findAll() {
        List<Film> films = queryForList(FIND_ALL);
        loadGenresForFilms(films);
        return films;
    }

    @Override
    public Film create(Film film) {
        Long id = insertAndGetId(INSERT,
                film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId());
        film.setId(id);
        updateGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        executeUpdate(UPDATE,
                film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        updateGenres(film);
        return film;
    }

    @Override
    public Optional<Film> findById(Long id) {
        Optional<Film> filmOpt = findOptional(FIND_BY_ID, id);
        filmOpt.ifPresent(film -> loadGenresForFilms(List.of(film)));
        return filmOpt;
    }

    @Override
    public Collection<Film> getPopular(int count) {
        List<Film> films = jdbcTemplate.query(FIND_POPULAR, rowMapper, count);
        loadGenresForFilms(films);
        return films;
    }

    private void updateGenres(Film film) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        if (film.getGenres() == null || film.getGenres().isEmpty()) return;

        List<Genre> genres = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, film.getId());
                        ps.setLong(2, genres.get(i).getId());
                    }
                    public int getBatchSize() { return genres.size(); }
                });
    }

    private void loadGenresForFilms(List<Film> films) {
        if (films.isEmpty()) return;

        String placeholders = Collections.nCopies(films.size(), "?").stream()
                .collect(Collectors.joining(","));

        String sql = "SELECT fg.film_id, g.genre_id, g.name " +
                "FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id IN (" + placeholders + ")";

        Object[] ids = films.stream().map(Film::getId).toArray();

        Map<Long, Set<Genre>> genresByFilmId = jdbcTemplate.query(sql, (ResultSet rs) -> {
            Map<Long, Set<Genre>> result = new HashMap<>();
            while (rs.next()) {
                result.computeIfAbsent(rs.getLong("film_id"), k -> new LinkedHashSet<>())
                        .add(new Genre(rs.getLong("genre_id"), rs.getString("name")));
            }
            return result;
        }, ids);

        films.forEach(film -> film.setGenres(genresByFilmId.getOrDefault(film.getId(), new LinkedHashSet<>())));
    }

    @Override public Film delete(Long id) { executeUpdate(DELETE, id); return null; }
    @Override public Long addLike(Long fId, Long uId) { executeUpdate(ADD_LIKE, fId, uId); return fId; }
    @Override public Long removeLike(Long fId, Long uId) { executeUpdate(REMOVE_LIKE, fId, uId); return fId; }
    @Override public boolean contains(Long id) { return exists(EXISTS, id); }
}