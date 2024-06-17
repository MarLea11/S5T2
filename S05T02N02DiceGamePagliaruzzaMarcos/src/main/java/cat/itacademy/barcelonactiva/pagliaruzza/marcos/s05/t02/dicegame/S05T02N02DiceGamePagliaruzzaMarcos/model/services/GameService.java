package cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.services;

import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.domain.Game;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.domain.Player;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.domain.User;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.exceptions.PlayerNotFoundException;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.exceptions.UserNotFoundException;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.repository.GameRepository;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.repository.PlayerRepository;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.repository.UserRepository;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.utils.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class GameService {

    @Autowired
    GameRepository gameRepository;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Converter converter;

    public Game createGame(String id, Authentication authentication) {
        Player player = validatePlayer(id, authentication);
        Random random = new Random();

        Game game = new Game();
        game.setDiceOne(random.nextInt(6) + 1);
        game.setDiceTwo(random.nextInt(6) + 1);
        game.setWon(game.getDiceOne() + game.getDiceTwo() == 7);
        gameRepository.save(game);
        player.getGamesList().add(game);
        playerRepository.save(player);

        return game;
    }

    public void deleteGame(String id, Authentication authentication) {
        Player player = validatePlayer(id, authentication);
        gameRepository.deleteAll(player.getGamesList());
        player.getGamesList().clear();
        playerRepository.save(player);
    }

    public List<Game> getAllGames(String id, Authentication authentication) {
       Player player = validatePlayer(id, authentication);
       return player.getGamesList();
    }

    public Player validatePlayer(String playerId, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return playerRepository.findByIdAndUserId(playerId, user.getId())
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
    }

}