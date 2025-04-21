package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.List;
import java.util.Optional;

/**
 * Класс для взаимодействия объектов MpaRating с базой данных
 */
@Slf4j
@Repository
@Primary
public class MpaRatingDbStorage extends BaseDbStorage<MpaRating> implements MpaRatingStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa_rating;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa_rating WHERE mpa_rating_id = ?;";

    public MpaRatingDbStorage(JdbcTemplate jdbc, RowMapper<MpaRating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<MpaRating> getAllMpaRatings() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public MpaRating getMpaRatingById(Integer id) {
        Optional<MpaRating> optionalMpa = findOne(FIND_BY_ID_QUERY, id);
        return optionalMpa.orElseThrow(() -> new NotFoundException("Рейтинг c ID: " + id + " не найден"));
    }
}
