package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.List;

/**
 * Класс для взаимодействия объектов MpaRating с базой данных
 */
@Slf4j
@Repository
@Primary
public class MpaRatingDbStorage implements MpaRatingStorage {
    private final JdbcOperations jdbc;
    private final RowMapper<MpaRating> mapper;

    @Autowired
    public MpaRatingDbStorage(JdbcOperations jdbc, RowMapper<MpaRating> mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public List<MpaRating> getAllMpaRatings() {
        final String FIND_ALL_QUERY = """
                SELECT *
                FROM mpa_rating;
                """;

        List<MpaRating> mpaRatings = jdbc.query(FIND_ALL_QUERY, mapper);
        if (mpaRatings == null || mpaRatings.isEmpty()) {
            return List.of();
        }
        return mpaRatings;
    }

    @Override
    public MpaRating getMpaRatingById(Integer id) {
        final String FIND_BY_ID_QUERY = """
                SELECT *
                FROM mpa_rating
                WHERE mpa_rating_id = ?;
                """;

        MpaRating mpa;
        try {
            mpa = jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            mpa = null;
        }
        if (mpa == null) {
            log.warn("MpaRatingDbStorage: Не удалось получить объект MpaRating по его ID - не найден в приложении");
            throw new NotFoundException("MpaRatingDbStorage: Рейтинг c ID: " + id + " не найден в приложении");
        }
        return mpa;
    }
}
