package Javabot.repository;
import Javabot.model.Joke;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface JokeRepository extends JpaRepository<Joke, Integer> {

    // Метод для получения всех шуток
    Page<Joke> findAll(Pageable pageable);

    // Метод для получения шутки по ID
    Optional<Joke> findById(Integer id);

    @Query(value = "SELECT MAX(id) FROM Joke")
    Integer findMaxId();

    @Query(value = "SELECT * FROM Joke ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Joke findRandomJoke();

    List<Joke> findTop5ByOrderByPopularityDesc();
}