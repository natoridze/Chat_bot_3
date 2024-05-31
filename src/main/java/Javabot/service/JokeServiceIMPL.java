package Javabot.service;

import Javabot.model.Joke;
import Javabot.repository.JokeHistoryRepository;
import Javabot.repository.JokeRepository;
import Javabot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JokeServiceIMPL implements JokeService {

    private final JokeRepository jokeRepository;
    private final UserRepository userRepository;
    private final JokeHistoryRepository jokeHistoryRepository;

    @Autowired
    public JokeServiceIMPL(JokeRepository jokeRepository, UserRepository userRepository, JokeHistoryRepository jokeHistoryRepository) {
        this.jokeRepository = jokeRepository;
        this.userRepository = userRepository;
        this.jokeHistoryRepository = jokeHistoryRepository;
    }

    @Override
    public Page<Joke> getAllJokes(int page, int size) {
        // Создаем объект Pageable с указанием номера страницы и размера страницы
        Pageable pageable = PageRequest.of(page, size);
        // Получаем страницу анекдотов из репозитория
        return jokeRepository.findAll(pageable);
    }

    @Override
    public Joke getJokeById(int id) {
        Optional<Joke> jokeOptional = jokeRepository.findById(id);
        return jokeOptional.orElse(null);
    }

    @Override
    public void createJoke(Joke joke) {
        if (joke.getText() == null || joke.getText().isEmpty()) {
            throw new IllegalArgumentException("Шутка не может быть пустой.");
        }
        jokeRepository.save(joke);
    }

    @Override
    public void updateJoke(int id, Joke updatedJoke) {
        // Check if the joke with the given ID exists
        if (!jokeRepository.existsById(id)) {
            throw new IllegalArgumentException("Joke with ID " + id + " does not exist.");
        }
        updatedJoke.setId(id);
        jokeRepository.save(updatedJoke);
    }
    @Override
    public void deleteJoke(int id) {
        jokeRepository.deleteById(id);
    }

    @Override
    public Joke findRandomJoke() {
        return jokeRepository.findRandomJoke();
    }
}