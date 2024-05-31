package Javabot.service;

import Javabot.model.Joke;
import Javabot.model.JokeHistory;
import Javabot.model.User;
import Javabot.repository.JokeHistoryRepository;
import Javabot.repository.JokeRepository;
import Javabot.repository.UserRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TelegramBotService  {
    private final TelegramBot telegramBot;
    private final JokeRepository jokeRepository;
    private final JokeHistoryRepository jokeHistoryRepository;
    private final UserRepository userRepository;
    private Joke currentJoke;
    public TelegramBotService(@Autowired TelegramBot telegramBot, @Autowired JokeRepository jokeRepository,UserRepository userRepository,JokeHistoryRepository jokeHistoryRepository  ) {
        this.telegramBot = telegramBot;
        this.jokeRepository = jokeRepository;
        this.userRepository = userRepository;
        this.jokeHistoryRepository = jokeHistoryRepository;
        this.telegramBot.setUpdatesListener(updates -> {
            updates.forEach(this::buttonClickReact);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, Throwable::printStackTrace);
    }
    private void buttonClickReact(Update update) {
        String text = update.message().text();
        if (text != null) { // Проверяем, что переменная text не равна null
            String username = update.message().from().username();

            if ("/help".equalsIgnoreCase(text)) {
                processHelpCommand(update);
            } else if ("/jokes".equalsIgnoreCase(text)) {
                sendAllJokes(update);
            } else if (text.startsWith("/jokes/")) {
                processJokeById(update, text);
            } else if ("/random".equalsIgnoreCase(text)){
                // Проверяем, существует ли пользователь с заданным userId
                if (!userRepository.existsByUsername(username)) {
                    addUserIfNotExists(username);
                }
                sendRandomJoke(update);
                currentJoke = sendRandomJoke(update);
                addJokeToHistory(currentJoke, username);
            }
        }
    }
    private void addUserIfNotExists(String username) {
        User newUser = new User();
        newUser.setUsername(username);
        userRepository.save(newUser);
    }
    private Joke sendRandomJoke(Update update) {
        // Извлекаем случайную шутку из репозитория
        Joke randomJoke = jokeRepository.findRandomJoke();
        // Получаем текст шутки
        randomJoke.setPopularity(randomJoke.getPopularity()+1);
        jokeRepository.save(randomJoke);
        String jokeText = randomJoke.getText();
        // Подготавливаем сообщение для отправки
        SendMessage request = new SendMessage(update.message().chat().id(), jokeText)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .disableNotification(true)
                .replyToMessageId(update.message().messageId());
        randomJoke.setPopularity(randomJoke.getPopularity()+1);
        // Отправляем сообщение
        this.telegramBot.execute(request);
        return randomJoke;
    }

    private void addJokeToHistory(Joke joke, String username) {
        if (joke != null && username != null) {
            // Ищем пользователя по его имени пользователя
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                JokeHistory jokeHistory = new JokeHistory();
                jokeHistory.setCallTime(LocalDateTime.now());
                jokeHistory.setUser(user);
                jokeHistory.setJoke(joke);
                jokeHistoryRepository.save(jokeHistory);
            } else {
                // Если пользователь не найден, выбрасываем исключение
                throw new RuntimeException("User not found");
            }
        }
    }

    private void processHelpCommand(Update update) {
        // Создайте сообщение с помощью текста, который описывает доступные команды
        String helpMessage = "Доступные команды:\n" +
                "/jokes - Получить список всех анекдотов\n" +
                "/jokes/{id} - Получить анекдот по ID\n" +
                "/jokes/random - Получить случайный анекдот\n" +
                "/help - Получить справку о доступных командах";

        // Отправьте сообщение с помощью бота
        SendMessage request = new SendMessage(update.message().chat().id(), helpMessage);
        telegramBot.execute(request);
    }
    private void sendAllJokes(Update update) {
        List<Joke> allJokes = jokeRepository.findAll();
        if (allJokes.isEmpty()) {
            SendMessage request = new SendMessage(update.message().chat().id(), "Список анекдотов пуст.");
            telegramBot.execute(request);
        } else {
            StringBuilder jokesList = new StringBuilder("Список анекдотов:\n");
            for (Joke joke : allJokes) {
                jokesList.append(joke.getId()).append(". ").append(joke.getText()).append("\n");
            }
            SendMessage request = new SendMessage(update.message().chat().id(), jokesList.toString());
            telegramBot.execute(request);
        }
    }

    private void processJokeById(Update update, String text) {
        // Разбираем команду и извлекаем ID анекдота
        String[] parts = text.split("/");
        if (parts.length != 3) {
            // Некорректный формат команды
            SendMessage request = new SendMessage(update.message().chat().id(), "Некорректный формат команды.");
            telegramBot.execute(request);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            // Неверный формат ID
            SendMessage request = new SendMessage(update.message().chat().id(), "ID анекдота должен быть целым числом.");
            telegramBot.execute(request);
            return;
        }
        // Получаем анекдот по ID из репозитория
        Optional<Joke> optionalJoke = jokeRepository.findById(id);
        if (optionalJoke.isPresent()) {
            Joke joke = optionalJoke.get();
            SendMessage request = new SendMessage(update.message().chat().id(), joke.getText());
            telegramBot.execute(request);
        } else {
            // Анекдот с указанным ID не найден
            SendMessage request = new SendMessage(update.message().chat().id(), "Анекдот с указанным ID не найден.");
            telegramBot.execute(request);
        }
    }
    private void sendTop5PopularJokes(Update update) {
        // Получаем список 5 популярных анекдотов
        List<Joke> top5Jokes = jokeRepository.findTop5ByOrderByPopularityDesc();

        if (top5Jokes.isEmpty()) {
            // Если список пуст, отправляем сообщение об этом
            SendMessage request = new SendMessage(update.message().chat().id(), "Список популярных анекдотов пуст.");
            telegramBot.execute(request);
        } else {
            // Формируем сообщение со списком популярных анекдотов
            StringBuilder jokeList = new StringBuilder("Топ 5 популярных анекдотов:\n");
            for (int i = 0; i < top5Jokes.size(); i++) {
                Joke joke = top5Jokes.get(i);
                jokeList.append(i + 1).append(". ").append(joke.getText()).append("\n");
            }

            // Отправляем сообщение с популярными анекдотами
            SendMessage request = new SendMessage(update.message().chat().id(), jokeList.toString());
            telegramBot.execute(request);
        }
    }
}