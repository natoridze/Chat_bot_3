package Javabot.repository;

import Javabot.model.JokeHistory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface JokeHistoryRepository extends JpaRepository<JokeHistory, Long> {

}
