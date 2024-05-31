package Javabot.controller;

import Javabot.model.Joke;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Javabot.service.JokeService;

import org.springframework.data.domain.Pageable;

@RestController
public class JokeController {
    @Autowired
    private JokeService jokeService;

    // Метод для выдачи всех анекдотов
    @GetMapping("/jokes")
    public Page<Joke> getAllJokes(int page, int size) {
        return jokeService.getAllJokes(page, size);
    }

    // Метод для выдачи анекдота по ID
    @GetMapping("/jokes/{id}")
    public Joke getJokeById(@PathVariable int id) {
        return jokeService.getJokeById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addJoke(@RequestBody Joke joke) {
        try {
            jokeService.createJoke(joke);
            return ResponseEntity.ok("Шутка успешно добавлена.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при добавлении шутки.");
        }
    }

    // Метод для изменения существующего анекдота
    @PutMapping("/jokes/{id}")
    public void updateJoke(@PathVariable int id, @RequestBody Joke updatedJoke) {
        jokeService.updateJoke(id, updatedJoke);
    }

    // Метод для удаления анекдота по ID
    @DeleteMapping("/jokes/{id}")
    public void deleteJoke(@PathVariable int id) {
        jokeService.deleteJoke(id);
    }

    @GetMapping("/jokes/random")
    public Joke findRandomJoke() {
        return jokeService.findRandomJoke();
    }
}