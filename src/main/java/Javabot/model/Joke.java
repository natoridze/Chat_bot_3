package Javabot.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class Joke {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "popularity")
    private int popularity;
    private String text;
    public Joke() {
    }
    public Joke(String text) {
        this.text = text;
    }

}