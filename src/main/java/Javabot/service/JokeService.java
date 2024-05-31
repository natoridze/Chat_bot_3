package Javabot.service;
import Javabot.model.Joke;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;

public interface JokeService {
    Page<Joke> getAllJokes(int page, int size);

    Joke getJokeById(int id);

    void createJoke(Joke joke);

    void updateJoke(int id, Joke updatedJoke);

    void deleteJoke(int id);
    Joke findRandomJoke();
}