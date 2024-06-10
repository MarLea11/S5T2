package cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.repository;

import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);
}
