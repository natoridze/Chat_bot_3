package Javabot.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Table(name = "user_roles")
@Entity(name = "user_roles")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private UserAuthority userAuthority;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
